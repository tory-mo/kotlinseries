package by.torymo.kotlinseries.ui.fragment



import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import by.torymo.kotlinseries.R
import by.torymo.kotlinseries.ui.adapters.SeriesListAdapter
import by.torymo.kotlinseries.data.db.Series
import by.torymo.kotlinseries.ui.model.SeriesListViewModel
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

        viewModel.getSeriesList().observe(viewLifecycleOwner, Observer<List<Series>>{ series ->
            series?.let { refreshSeriesList(series) }
        })
    }

    private fun refreshSeriesList(series: List<Series>){
        lvSeries.adapter = SeriesListAdapter(series, this)
    }
}