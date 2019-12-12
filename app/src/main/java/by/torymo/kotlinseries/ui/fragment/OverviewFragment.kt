package by.torymo.kotlinseries.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import by.torymo.kotlinseries.DateTimeUtils
import by.torymo.kotlinseries.R
import by.torymo.kotlinseries.data.db.Season
import by.torymo.kotlinseries.data.db.Series
import by.torymo.kotlinseries.ui.DetailActivity
import by.torymo.kotlinseries.ui.adapters.SeasonsAdapter
import kotlinx.android.synthetic.main.fragment_overview.*
import org.json.JSONObject
import java.io.Serializable

class OverviewFragment: Fragment(){

    private var seasonsAdapter: SeasonsAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        seasonsAdapter = SeasonsAdapter(activity as DetailActivity)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_overview, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val series = this.arguments?.getSerializable(SERIES_PARAM) as Series?
        series?.let {
            fillInData(series)
        }

        rvSeasons.adapter = seasonsAdapter
    }

    fun fillInData(series: Series){
        tvOngoing?.text = getString(if(series.inProduction)R.string.format_ongoing_true else R.string.format_ongoing_false)
        tvFirstDate?.text = DateTimeUtils.format(series.firstAirDate)
        tvGenres?.text = series.genres
        if(series.lastAirDate > 0)
            tvLastDate?.text = DateTimeUtils.format(series.lastAirDate)
        tvNetworks?.text = series.networks

        val ln = activity?.resources?.getStringArray(R.array.lang)
        val currLn = ln?.filter{
            val json = JSONObject(it)
            json.getString("code") == series.originalLanguage}
        tvOriginalLng?.text = if(currLn == null || currLn.isEmpty()) series.originalLanguage else JSONObject(currLn[0]).getString("name")
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

    fun updateSeason(persistentSeries: Boolean){
        seasonsAdapter?.setCheckbox(persistentSeries)
    }

    fun updateSeason(seasons: List<Season>, persistentSeries: Boolean){
        seasonsAdapter?.setItems(seasons, persistentSeries)
    }

    companion object {
        const val SERIES_PARAM = "SERIES_EXTRA"

        fun newInstance(series: Series): Fragment{
            val fragment = OverviewFragment()
            val args = Bundle()
            args.putSerializable(SERIES_PARAM, series as Serializable)
            fragment.arguments = args
            return fragment
        }
    }
}