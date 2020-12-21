package by.torymo.kotlinseries.ui.fragment

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import by.torymo.kotlinseries.R
import by.torymo.kotlinseries.SearchNavigationDirections
import by.torymo.kotlinseries.data.db.Series
import by.torymo.kotlinseries.ui.adapters.SeriesListAdapter
import by.torymo.kotlinseries.ui.model.SearchViewModel
import kotlinx.android.synthetic.main.fragment_search.*
import kotlinx.android.synthetic.main.fragment_series.*
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


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

        viewModel.clearSearch()

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                searchView.clearFocus()
                lifecycleScope.launch {
                    viewModel.searchSeries(query).collectLatest {series->
                        seriesListAdapter.submitData(series)
                    }
                }


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

    fun hideKeyboard(activity: Activity) {
        val imm: InputMethodManager = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        //Find the currently focused view, so we can grab the correct window token from it.
        var view = activity.currentFocus
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = View(activity)
        }
        imm.hideSoftInputFromWindow(view.windowToken, 0)
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

    override fun onItemClick(series: Series, item: View) {
        val action = SearchNavigationDirections.toSearchDetails()
        action.setSeriesId(series.id)

        val navController = findNavController()
        navController.navigate(action)
    }
}