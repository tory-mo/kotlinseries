package by.torymo.kotlinseries.ui.fragment

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders

import by.torymo.kotlinseries.R
import by.torymo.kotlinseries.data.db.Series
import by.torymo.kotlinseries.picasso
import by.torymo.kotlinseries.ui.model.SeriesDetailsViewModel
import kotlinx.android.synthetic.main.fragment_detail.*


class DetailFragment : Fragment() {

    private lateinit var viewModel: SeriesDetailsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /*val seriesId = DetailFragmentArgs.fromBundle(arguments).seriesId

        viewModel = ViewModelProviders.of(this).get(SeriesDetailsViewModel::class.java)

        viewModel.getSeriesById(seriesId).observe(this, Observer<Series>{ series ->
            series?.let { refreshSeries(series) }
        })
        viewModel.getSeriesDetailsFr(seriesId, object : DetailCallback{
            override fun onError(message: String?) {
                Toast.makeText(activity, message, Toast.LENGTH_LONG).show()
            }
        })*/

    }

    private fun refreshSeries(series: Series){
        activity?.supportFragmentManager?.let {
            ep_view_pager.adapter = DetailsPagerAdapter(it, if (series.temporary) 1 else 2, series, context)
            tab_layout.setupWithViewPager(ep_view_pager)
        }

        fbFavourite.setImageDrawable(context?.getDrawable(if(series.temporary) R.drawable.ic_not_favorite else R.drawable.ic_favorite))

        tvSeriesName.text = series.name
        tvSeriesOriginalName.text = series.originalName

        ivEpisodesHeader.picasso(series.backdrop)
    }

    class DetailsPagerAdapter(fragmentManager: FragmentManager, private val count: Int, private val series: Series, private val context: Context?): FragmentPagerAdapter(fragmentManager) {

        private val TITLES = arrayOf(R.string.overview, R.string.episodes)

        override fun getCount(): Int {
            return count
        }

        override fun getItem(position: Int): Fragment {
            return when(position){
                1 -> {
                    val ef = EpisodesFragment()
                    ef
                }
                else -> OverviewFragment.newInstance(series)
            }
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return context?.getString(TITLES[position])
        }
    }

    interface DetailCallback{
        fun onError(message: String?)
    }

}
