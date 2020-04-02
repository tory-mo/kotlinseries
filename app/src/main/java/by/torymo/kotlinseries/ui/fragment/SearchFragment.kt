package by.torymo.kotlinseries.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import by.torymo.kotlinseries.R
import by.torymo.kotlinseries.SearchNavigationDirections
import by.torymo.kotlinseries.data.db.Series
import by.torymo.kotlinseries.ui.adapters.SeriesListAdapter
import by.torymo.kotlinseries.ui.model.SearchViewModel
import kotlinx.android.synthetic.main.fragment_search.*

class SearchFragment: Fragment(), SeriesListAdapter.OnItemClickListener {

    private lateinit var viewModel: SearchViewModel
    private lateinit var seriesListAdapter: SeriesListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        viewModel = ViewModelProvider(this).get(SearchViewModel::class.java)
        seriesListAdapter = SeriesListAdapter(this)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lvSeries.adapter = seriesListAdapter

        viewModel.clearSearch()
        viewModel.seriesList.observe(viewLifecycleOwner, Observer<List<Series>>{ series ->
            series?.let { refreshSeriesList(series) }
        })

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {

                viewModel.clearSearch()
                viewModel.searchSeries(query, 1, object : SearchCallback {
                    override fun onSuccess(series: List<Series>) {

                    }

                    override fun onError(message: String?) {
                        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                    }
                })
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                return true
            }
        })

        searchView.setOnCloseListener {
            viewModel.clearSearch()
            searchView?.onActionViewCollapsed()
            true
        }
    }

    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity).run {
            setSupportActionBar(searchToolbar)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }

        searchToolbar.setNavigationOnClickListener {
            activity?.onBackPressed()
        }
    }

    private fun refreshSeriesList(series: List<Series>){
        seriesListAdapter.setItems(series)
    }

    interface SearchCallback{
        fun onSuccess(series: List<Series>)
        fun onError(message: String?)
    }

    override fun onItemClick(series: Series, item: View) {
        val action = SearchNavigationDirections.toSearchDetails()
        action.setSeriesId(series.id)

        val navController = findNavController()
        navController.navigate(action)
    }
}