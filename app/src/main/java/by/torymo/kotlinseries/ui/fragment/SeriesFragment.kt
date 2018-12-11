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
import androidx.navigation.fragment.NavHostFragment


class SeriesFragment: Fragment(), SeriesListAdapter.OnItemClickListener,
        SearchView.OnQueryTextListener,
        SearchView.OnCloseListener {

    private lateinit var viewModel: SeriesListViewModel
    private lateinit var searchView: SearchView

    override fun onItemClick(series: Series, item: View) {

        val action = SeriesFragmentDirections.actionBottomSeriesToDetailActivity()
        action.setSeriesId(series.mdbId)
        val navController = view?.findNavController()
        navController?.navigate(action)

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

        viewModel.seriesList.observe(viewLifecycleOwner, Observer<List<Series>>{ series ->
            series?.let { refreshSeriesList(series) }
        })
    }

    private fun refreshSeriesList(series: List<Series>){
        lvSeries.adapter = SeriesListAdapter(series, this)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater?.inflate(R.menu.series_list_menu, menu)

        searchView = menu?.findItem(R.id.action_search)?.actionView as SearchView
        searchView.setOnQueryTextListener(this)
        searchView.setOnCloseListener(this)
    }

    override fun onClose(): Boolean {
        viewModel.clearSearch()
        searchView.onActionViewCollapsed()
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean = true

    //TODO: paging
    override fun onQueryTextSubmit(query: String?): Boolean {
        query?.let {
            viewModel.searchSeries(query, 1, object : SearchCallback{
                override fun onSuccess(series: List<Series>) {
                    viewModel.getSearchResult()
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