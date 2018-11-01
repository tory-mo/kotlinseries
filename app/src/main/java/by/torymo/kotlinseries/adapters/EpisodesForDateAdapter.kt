package by.torymo.kotlinseries.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import by.torymo.kotlinseries.R
import by.torymo.kotlinseries.domain.Episode
import kotlinx.android.synthetic.main.episode_list_item.view.*

class EpisodesForDateAdapter(private val items: List<Episode>,
                             private val clickListener: EpisodesForDateAdapter.OnItemClickListener):
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    interface OnItemClickListener{
        fun onItemClick(episode: Episode, item: View)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.episode_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ViewHolder).bind(items[position], clickListener)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(episode: Episode, listener: EpisodesForDateAdapter.OnItemClickListener) = with(itemView) {
            tvName.text = episode.name
            tvDate.text = resources.getString(R.string.format_episode_number, episode.episodeNumber, episode.seasonNumber)
            tvEpisodeInfo.text = episode.seriesName
            if(episode.seen){
                ivSeenIcon.setImageResource(R.drawable.eye)
            }else{
                ivSeenIcon.setImageResource(R.drawable.eye_off)
            }

            // RecyclerView on item click
            setOnClickListener {
                listener.onItemClick(episode, it)
            }
        }
    }
}