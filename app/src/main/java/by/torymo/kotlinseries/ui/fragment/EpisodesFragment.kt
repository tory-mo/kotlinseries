package by.torymo.kotlinseries.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import by.torymo.kotlinseries.R
import by.torymo.kotlinseries.data.db.ExtendedEpisode
import by.torymo.kotlinseries.picasso
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



        viewModel = ViewModelProvider(this).get(EpisodeListViewModel::class.java)

        episodesListAdapter = EpisodeListAdapter(this)
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_episodes, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val args = EpisodesFragmentArgs.fromBundle(arguments)

        val seasonId = args.seasonId
        val seasonName = args.seasonName
        val seasonPoster = args.poster
        val seriesName = args.seriesName

        collapsing_toolbar?.title = seasonName
        toolbar.title = seasonName
        tvSeriesName.text = seriesName

        seasonPoster?.let {
            ivEpisodesHeader.picasso(it)
        }

        lvEpisodes.adapter = episodesListAdapter
        val decoration = DividerItemDecoration(activity, DividerItemDecoration.VERTICAL)
        lvEpisodes.addItemDecoration(decoration)

        seasonId.run {
            if(this == 0L) return
            viewModel.getEpisodesByMdbId(this).observe(viewLifecycleOwner, Observer<List<ExtendedEpisode>>{ episodes ->
                episodes?.let { refreshEpisodeList(episodes) }
            })
        }
    }

    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity).run {
            setSupportActionBar(toolbar)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)

        }
        toolbar.setNavigationOnClickListener { findNavController().navigateUp() }
    }

    private fun refreshEpisodeList(episodes: List<ExtendedEpisode>){
        episodesListAdapter.setItems(episodes)
    }

}