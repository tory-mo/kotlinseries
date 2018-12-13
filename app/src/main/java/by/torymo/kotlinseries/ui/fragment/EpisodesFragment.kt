package by.torymo.kotlinseries.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import by.torymo.kotlinseries.R
import by.torymo.kotlinseries.data.db.Episode
import by.torymo.kotlinseries.ui.model.EpisodeListViewModel


class EpisodesFragment: Fragment() {

    private lateinit var viewModel: EpisodeListViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        viewModel = ViewModelProviders.of(this).get(EpisodeListViewModel::class.java)
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_series, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val seriesId = this.arguments?.getString(SERIES_ID_PARAM)

        seriesId?.let {
            viewModel.getEpisodesByMdbId(seriesId).observe(viewLifecycleOwner, Observer<List<Episode>>{ episodes ->
                episodes?.let { refreshEpisodeList(episodes) }
            })
        }

    }

    private fun refreshEpisodeList(episodes: List<Episode>){}

    companion object {
        val SERIES_ID_PARAM = "SERIES_ID_EXTRA"

        fun newInstance(seriesId: String): Fragment{
            val fragment = OverviewFragment()
            val args = Bundle()
            args.putString(SERIES_ID_PARAM, seriesId)
            fragment.arguments = args
            return fragment
        }
    }

}