package by.torymo.kotlinseries.ui.fragment

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import by.torymo.kotlinseries.R
import by.torymo.kotlinseries.ui.adapters.SeriesListAdapter
import by.torymo.kotlinseries.data.db.Series
import by.torymo.kotlinseries.ui.model.SeriesListViewModel
import kotlinx.android.synthetic.main.fragment_series.*
import androidx.appcompat.widget.SearchView

class SeriesFragment: Fragment(), SeriesListAdapter.OnItemClickListener,
        SearchView.OnQueryTextListener,
        SearchView.OnCloseListener{

    private lateinit var viewModel: SeriesListViewModel
    private var searchView: SearchView? = null

    private lateinit var seriesListAdapter: SeriesListAdapter



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        viewModel = ViewModelProviders.of(this).get(SeriesListViewModel::class.java)

        seriesListAdapter = SeriesListAdapter(this)
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_series, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //(activity as AppCompatActivity).setSupportActionBar(toolbar)

        lvSeries.adapter = seriesListAdapter

        viewModel.clearSearch()
        viewModel.seriesList.observe(viewLifecycleOwner, Observer<List<Series>>{ series ->
            series?.let { refreshSeriesList(series) }
        })
    }

    private fun refreshSeriesList(series: List<Series>){
         seriesListAdapter.setItems(series)
    }

    override fun onItemClick(series: Series, item: View) {
        val action = SeriesTabLayoutFragmentDirections.actionBottomSeriesToDetailActivity()
        action.setSeriesId(series.id)

        val navController = view?.findNavController()
        navController?.navigate(action)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        menu.clear()
        activity?.menuInflater?.inflate(R.menu.series_list_menu, menu)

        searchView = menu.findItem(R.id.action_search)?.actionView as SearchView
        searchView?.setOnQueryTextListener(this)
        searchView?.setOnCloseListener(this)
    }

    override fun onClose(): Boolean {
        viewModel.clearSearch()
        searchView?.onActionViewCollapsed()
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean = true

    //TODO: paging
    override fun onQueryTextSubmit(query: String?): Boolean {
        query?.let {
            viewModel.clearSearch()
            viewModel.searchSeries(query, 1, object : SearchCallback{
                override fun onSuccess(series: List<Series>) {

                }

                override fun onError(message: String?) {
                    Toast.makeText(activity, message, Toast.LENGTH_LONG).show()
                }
            })
        }
        return true
    }

    interface SearchCallback{
        fun onSuccess(series: List<Series>)
        fun onError(message: String?)
    }
}