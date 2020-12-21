package by.torymo.kotlinseries.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import by.torymo.kotlinseries.MainNavHostDirections
import by.torymo.kotlinseries.R
import by.torymo.kotlinseries.data.SeriesRepository
import by.torymo.kotlinseries.data.db.Series
import by.torymo.kotlinseries.ui.adapters.SeriesListAdapter
import by.torymo.kotlinseries.ui.model.SeriesListViewModel
import kotlinx.android.synthetic.main.fragment_series.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.io.Serializable

class SeriesListFragment: Fragment(), SeriesListAdapter.OnItemClickListener {

    private lateinit var viewModel: SeriesListViewModel
    private lateinit var seriesListAdapter: SeriesListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        seriesListAdapter = SeriesListAdapter(this)
        viewModel = ViewModelProvider(this).get(SeriesListViewModel::class.java)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_series, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val type = this.arguments?.getSerializable(TYPE_PARAM) as SeriesRepository.Companion.SeriesType?
        lvSeries.adapter = seriesListAdapter

        val decoration = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
        lvSeries.addItemDecoration(decoration)

        lvSeries.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if ((recyclerView.layoutManager as LinearLayoutManager)
                                .findFirstCompletelyVisibleItemPosition() == 0) {
                    fbUp.visibility = View.GONE
                }else{
                    fbUp.visibility = View.VISIBLE
                }
            }
        })

        fbUp.setOnClickListener {
            lvSeries.smoothScrollToPosition(0)
        }

        type?.let {
           lifecycleScope.launch {
               viewModel.seriesList(it).collectLatest {series->
                   seriesListAdapter.submitData(series)
               }
           }
        }
    }

    override fun onItemClick(series: Series, item: View) {
        val action = MainNavHostDirections.toDetails()
        action.setSeriesId(series.id)

        val navController = view?.findNavController()
        navController?.navigate(action)
    }

    companion object {
        const val TYPE_PARAM = "TYPE_EXTRA"

        fun newInstance(type: SeriesRepository.Companion.SeriesType): Fragment{
            val fragment = SeriesListFragment()
            val args = Bundle()
            args.putSerializable(TYPE_PARAM, type as Serializable)
            fragment.arguments = args
            return fragment
        }
    }
}