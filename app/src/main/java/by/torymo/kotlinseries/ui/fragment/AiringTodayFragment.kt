package by.torymo.kotlinseries.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import by.torymo.kotlinseries.R
import by.torymo.kotlinseries.data.db.Series
import by.torymo.kotlinseries.ui.adapters.SeriesListAdapter
import by.torymo.kotlinseries.ui.model.AiringTodayViewModel
import kotlinx.android.synthetic.main.fragment_series.*

class AiringTodayFragment: Fragment(), SeriesListAdapter.OnItemClickListener {

    private lateinit var viewModel: AiringTodayViewModel
    private lateinit var seriesListAdapter: SeriesListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        seriesListAdapter = SeriesListAdapter(this)

        viewModel = ViewModelProviders.of(this).get(AiringTodayViewModel::class.java)

    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        menu.clear()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_series, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lvSeries.adapter = seriesListAdapter
        viewModel.seriesList.observe(viewLifecycleOwner, Observer { series->
            seriesListAdapter.setItems(series)
        })
    }

    override fun onItemClick(series: Series, item: View) {
        val action = SeriesTabLayoutFragmentDirections.actionBottomSeriesToDetailActivity()
        action.setSeriesId(series.id)

        val navController = view?.findNavController()
        navController?.navigate(action)
    }
}