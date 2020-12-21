package by.torymo.kotlinseries.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import by.torymo.kotlinseries.DateTimeUtils
import by.torymo.kotlinseries.R
import by.torymo.kotlinseries.data.db.Season
import by.torymo.kotlinseries.data.db.Series
import by.torymo.kotlinseries.picasso
import by.torymo.kotlinseries.ui.adapters.CastAdapter
import by.torymo.kotlinseries.ui.adapters.SeasonsAdapter
import by.torymo.kotlinseries.ui.model.SeriesDetailsViewModel
import kotlinx.android.synthetic.main.fragment_series_detail.*
import kotlinx.coroutines.launch
import org.json.JSONObject

class SeriesDetailsFragment : Fragment(), SeasonsAdapter.OnItemClickListener {

    private lateinit var viewModel: SeriesDetailsViewModel

    private var seriesId: Long = 0
    private var currSeries: Series? = null
    private var following: Boolean = false

    private var seasonsAdapter: SeasonsAdapter? = null
    private lateinit var castAdapter: CastAdapter

    override fun onItemClick(season: Season, item: View) {
        val action = SeriesDetailsFragmentDirections.toEpisodes()
        action.poster = season.poster
        action.seasonId = season.id
        action.seasonName = season.name
        currSeries?.let {
            action.setSeriesName(it.name)
        }
        val navController = view?.findNavController()
        navController?.navigate(action)
    }

    override fun onItemMenuClick(season: Season, item: View) {
        viewModel.changeSeasonFollowing(season, !following)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activity?.intent?.extras?.let {
            seriesId = SeriesDetailsFragmentArgs.fromBundle(it).seriesId
        }
        seasonsAdapter = SeasonsAdapter(this)
        castAdapter = CastAdapter()
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

        lifecycleScope.launch {
           val (series, seasons, cast) = viewModel.getSeriesDetails(seriesId)
            refreshSeries(series)

            cast?.let {
                castAdapter.setItems(it)
            }

            seasons?.let {
                updateSeason(it, following)
            }

            val checkedSeries = viewModel.checkFollowing(seriesId)
            if(checkedSeries != null) following = true
        }

        viewModel.getSeasons(seriesId).observe(viewLifecycleOwner, Observer<List<Season>> {
            it?.let {
                val tmpSeries = currSeries
                updateSeason(it, following)
            }
        })

        rvCast.adapter = castAdapter
        rvSeasons.adapter = seasonsAdapter
        val decoration = DividerItemDecoration(activity, DividerItemDecoration.VERTICAL)
        rvSeasons.addItemDecoration(decoration)

        fbFavourite.setOnClickListener{
            favouriteStatusChanged()
        }
    }

    private fun fillInData(series: Series){
        var info = DateTimeUtils.formatYear(series.firstAirDate)
        info += if(series.inProduction) " - ..." else if(DateTimeUtils.formatYear(series.lastAirDate) != info) " - " + DateTimeUtils.formatYear(series.lastAirDate) else ""
        info += if(series.episodeRunTime > 0) " | " + series.episodeRunTime.toString() + "m" else ""
        info += if(series.certification.isNotEmpty()) " | " + series.certification else ""
        tvInfo.text = info

        tvGenres?.text = series.genres
        if(series.nextEpisodeDate > 0) {
            tvLastDate?.text = DateTimeUtils.format(series.nextEpisodeDate)
            tvLastDate?.visibility = View.VISIBLE
            tvLastDateLabel?.visibility = View.VISIBLE
        }else{
            tvLastDate?.visibility = View.GONE
            tvLastDateLabel?.visibility = View.GONE
        }
        tvNetworks?.text = series.networks

        val ln = resources.getStringArray(R.array.lang)
        val currLn = ln.filter{
            val json = JSONObject(it)
            json.getString("code") == series.originalLanguage}
        //tvOriginalLng?.text = if(currLn.isEmpty()) series.originalLanguage else JSONObject(currLn[0]).getString("name")
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
        seasonsAdapter?.setItems(seasons.reversed(), persistentSeries)
    }

    private fun refreshSeries(series: Series){
        currSeries = series

        fillInData(series)

        context?.let {
            fbFavourite.setImageDrawable(ContextCompat.getDrawable(it, if(following) R.drawable.ic_favorite else R.drawable.ic_not_favorite))
        }

        collapsing_toolbar?.title = series.name
        //title = series.name
        tvSeriesOriginalName.text = series.originalName

        ivEpisodesHeader.picasso(series.backdrop)
    }

    private fun favouriteStatusChanged(){
        val series = currSeries ?: return

        following = !following

        viewModel.seriesFollowingStatusChanged(series, following)
        updateSeason(following)
        context?.let {
            fbFavourite.setImageDrawable(ContextCompat.getDrawable(it, if(following) R.drawable.ic_favorite else R.drawable.ic_not_favorite))
        }

    }
}
