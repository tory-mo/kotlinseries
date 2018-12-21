package by.torymo.kotlinseries.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import by.torymo.kotlinseries.R
import by.torymo.kotlinseries.Utility
import by.torymo.kotlinseries.data.db.Series
import by.torymo.kotlinseries.ui.DetailActivity
import kotlinx.android.synthetic.main.fragment_overview.*
import org.json.JSONObject
import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.*

class OverviewFragment: Fragment()/*, DetailActivity.SeriesUpdatedCallback*/  {


    /*override fun onUpdated(series: Series) {
        fillInData(series)
    }*/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_overview, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val series = this.arguments?.getSerializable(SERIES_PARAM) as Series
        fillInData(series)
        //(activity as DetailActivity).setUpdatedCallback(this)

    }

    private fun fillInData(series: Series){
        tvOngoing.text = getString(if(series.inProduction)R.string.format_ongoing_true else R.string.format_ongoing_false)
        tvSeasons.text = series.seasons.toString()
        tvFirstDate.text = SimpleDateFormat(Utility.dateToStrFormat, Locale.getDefault()).format(series.firstAirDate)
        tvGenres.text = series.genres
        tvHomepage.text = series.homepage
        if(series.lastAirDate > 0)
            tvLastDate.text = SimpleDateFormat(Utility.dateToStrFormat, Locale.getDefault()).format(series.lastAirDate)
        tvNetworks.text = series.networks

        val ln = activity?.resources?.getStringArray(R.array.lang)
//        for(i in 0..ln.size){
//            val json = JSONObject(ln[i])
//            if(json.getString("code").equals(series.originalLanguage)){
//                tvOriginalLng.text = json.getString("name")
//            }
//        }
        val currLn = ln?.filter{
            val json = JSONObject(it)
            json.getString("code") == series.originalLanguage}
        tvOriginalLng.text = if(currLn == null || currLn.isEmpty()) series.originalLanguage else JSONObject(currLn[0]).getString("name")
        //series.popularity
        //series.voteAverage
        //series.voteCount


        tvOverview.text = series.overview
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