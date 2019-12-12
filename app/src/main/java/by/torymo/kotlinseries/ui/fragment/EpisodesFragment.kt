package by.torymo.kotlinseries.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import by.torymo.kotlinseries.R
import by.torymo.kotlinseries.data.db.ExtendedEpisode
import by.torymo.kotlinseries.ui.adapters.EpisodeListAdapter
import by.torymo.kotlinseries.ui.model.EpisodeListViewModel
import kotlinx.android.synthetic.main.fragment_episodes.*


class EpisodesFragment: Fragment(), EpisodeListAdapter.OnItemClickListener {

    override fun onItemClick(episode: ExtendedEpisode, item: View) {
        episode.id?.let {
            viewModel.changeEpisodeSeen(it, !episode.seen)
        }
    }

    private lateinit var viewModel: EpisodeListViewModel
    private lateinit var episodesListAdapter: EpisodeListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        viewModel = ViewModelProviders.of(this).get(EpisodeListViewModel::class.java)

        episodesListAdapter = EpisodeListAdapter(this)
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_episodes, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val seriesId = this.arguments?.getLong(SERIES_ID_PARAM)

        lvEpisodes.adapter = episodesListAdapter

        seriesId?.let {
            viewModel.getEpisodesByMdbId(seriesId).observe(viewLifecycleOwner, Observer<List<ExtendedEpisode>>{ episodes ->
                episodes?.let { refreshEpisodeList(episodes) }
            })
        }
    }

    private fun refreshEpisodeList(episodes: List<ExtendedEpisode>){
        episodesListAdapter.setItems(episodes)
    }

    companion object {
        const val SERIES_ID_PARAM = "SERIES_ID_EXTRA"

        fun newInstance(seriesId: Long): Fragment{
            val fragment = EpisodesFragment()
            val args = Bundle()
            args.putLong(SERIES_ID_PARAM, seriesId)
            fragment.arguments = args
            return fragment
        }
    }

}