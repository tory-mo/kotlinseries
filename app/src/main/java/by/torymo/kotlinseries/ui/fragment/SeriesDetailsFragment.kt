package by.torymo.kotlinseries.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import by.torymo.kotlinseries.DateTimeUtils
import by.torymo.kotlinseries.R
import by.torymo.kotlinseries.data.db.Season
import by.torymo.kotlinseries.data.db.Series
import by.torymo.kotlinseries.picasso
import by.torymo.kotlinseries.ui.adapters.SeasonsAdapter
import by.torymo.kotlinseries.ui.model.SeriesDetailsViewModel
import kotlinx.android.synthetic.main.fragment_series_detail.*
import org.json.JSONObject

class SeriesDetailsFragment : Fragment(), SeasonsAdapter.OnItemClickListener {

    private lateinit var viewModel: SeriesDetailsViewModel

    private var seriesId: Long = 0
    private var currSeries: Series? = null

    private var seasonsAdapter: SeasonsAdapter? = null

    override fun onItemClick(season: Season, item: View) {
        val action = SeriesDetailsFragmentDirections.toEpisodes()
        action.setPoster(season.poster)
        action.setSeasonId(season.id)
        action.setSeasonName(season.name)
        currSeries?.let {
            action.setSeriesName(it.name)
        }
        val navController = view?.findNavController()
        navController?.navigate(action)
    }

    override fun onItemMenuClick(season: Season, item: View) {
        val tmpSeries = currSeries
        viewModel.changeSeasonFollowing(season, if(tmpSeries == null) false else !tmpSeries.temporary)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        seriesId = SeriesDetailsFragmentArgs.fromBundle(activity?.intent?.extras).seriesId
        seasonsAdapter = SeasonsAdapter(this)
    }

    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity).run {
            setSupportActionBar(toolbar)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
        toolbar.setNavigationOnClickListener {
            activity?.onBackPressed()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_series_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this).get(SeriesDetailsViewModel::class.java)
        viewModel.getSeriesById(seriesId).observe(viewLifecycleOwner, Observer<Series>{ series ->
            series?.let {
                refreshSeries(series)
            }
        })
        viewModel.getSeriesDetails(seriesId, object : DetailCallback {
            override fun onError(message: String?) {
                Toast.makeText(activity, message, Toast.LENGTH_LONG).show()
            }
        })
        viewModel.getSeasons(seriesId).observe(viewLifecycleOwner, Observer<List<Season>> {
            it?.let {
                val tmpSeries = currSeries
                updateSeason(it, if(tmpSeries != null) !tmpSeries.temporary else false)
            }
        })

        rvSeasons.adapter = seasonsAdapter
        val decoration = DividerItemDecoration(activity, DividerItemDecoration.VERTICAL)
        rvSeasons.addItemDecoration(decoration)

        fbFavourite.setOnClickListener{
            favouriteStatusChanged()
        }
    }

    private fun fillInData(series: Series){
        tvOngoing?.text = getString(if(series.inProduction)R.string.format_ongoing_true else R.string.format_ongoing_false)
        tvFirstDate?.text = DateTimeUtils.format(series.firstAirDate)
        tvGenres?.text = series.genres
        if(series.lastAirDate > 0)
            tvLastDate?.text = DateTimeUtils.format(series.lastAirDate)
        tvNetworks?.text = series.networks

        val ln = resources.getStringArray(R.array.lang)
        val currLn = ln.filter{
            val json = JSONObject(it)
            json.getString("code") == series.originalLanguage}
        tvOriginalLng?.text = if(currLn.isEmpty()) series.originalLanguage else JSONObject(currLn[0]).getString("name")
        //series.popularity
        //series.voteAverage
        //series.voteCount

        if(series.homepage.isEmpty()){
            tvHomepage?.visibility = View.GONE
        }else {
            tvHomepage?.visibility = View.VISIBLE
            tvHomepage?.text = series.homepage
        }

        if(series.overview.isEmpty()){
            tvOverview?.visibility = View.GONE
        }else {
            tvOverview?.visibility = View.VISIBLE
            tvOverview?.text = series.overview
        }

        if(series.homepage.isEmpty() && series.overview.isEmpty()){
            vDivider2?.visibility = View.GONE
        }else{
            vDivider2?.visibility = View.VISIBLE
        }
    }

    private fun updateSeason(persistentSeries: Boolean){
        seasonsAdapter?.setCheckbox(persistentSeries)
    }

    private fun updateSeason(seasons: List<Season>, persistentSeries: Boolean){
        seasonsAdapter?.setItems(seasons, persistentSeries)
    }

    private fun refreshSeries(series: Series){
        currSeries = series

        fillInData(series)

        fbFavourite.setImageDrawable(activity?.getDrawable(if(series.temporary) R.drawable.ic_not_favorite else R.drawable.ic_favorite))

        collapsing_toolbar?.title = series.name
        //title = series.name
        tvSeriesOriginalName.text = series.originalName

        ivEpisodesHeader.picasso(series.backdrop)
    }

    private fun favouriteStatusChanged(){
        val series = currSeries ?: return

        viewModel.seriesFollowingStatusChanged(series)
        updateSeason(series.temporary)
        fbFavourite.setImageDrawable(activity?.getDrawable(if(!series.temporary) R.drawable.ic_not_favorite else R.drawable.ic_favorite))
    }

    interface DetailCallback{
        fun onError(message: String?)
    }

}
