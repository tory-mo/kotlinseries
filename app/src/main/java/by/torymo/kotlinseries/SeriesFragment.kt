package by.torymo.kotlinseries

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import by.torymo.kotlinseries.adapters.SeriesListAdapter
import by.torymo.kotlinseries.domain.Series
import by.torymo.kotlinseries.ui.SeriesListViewModel
import kotlinx.android.synthetic.main.fragment_series.*

class SeriesFragment: Fragment(), SeriesListAdapter.OnItemClickListener {

    private lateinit var viewModel: SeriesListViewModel

    override fun onItemClick(series: Series, item: View) {
        val seriesBundle = Bundle().apply {
            putString(getString(R.string.extra_key_series_id), series.mdbId)
        }
        view?.findNavController()
                ?.navigate(R.id.action_bottomNavFragment2_to_fragmentEpisodes, seriesBundle)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        viewModel = ViewModelProviders.of(this).get(SeriesListViewModel::class.java)
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_series, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getSeriesList().observe(this, Observer<List<Series>>{ series ->
            series?.let { refreshSeriesList(series) }
        })
    }

    private fun refreshSeriesList(series: List<Series>){
        lvSeries.adapter = SeriesListAdapter(series, this)
    }
}