package by.torymo.kotlinseries.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import by.torymo.kotlinseries.R
import by.torymo.kotlinseries.data.db.ExtendedEpisode
import by.torymo.kotlinseries.picasso
import kotlinx.android.synthetic.main.episode_item.view.*
import kotlinx.android.synthetic.main.episode_list_item.view.ivSeenIcon
import kotlinx.android.synthetic.main.episode_list_item.view.tvDate
import kotlinx.android.synthetic.main.episode_list_item.view.tvEpisodeInfo
import kotlinx.android.synthetic.main.episode_list_item.view.tvName

class EpisodesForDateAdapter(private val clickListener: OnItemClickListener):
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    interface OnItemClickListener{
        fun onItemClick(episode: ExtendedEpisode, item: View)
    }

    private var items: List<ExtendedEpisode> = mutableListOf()

    fun updateItems(newItems: List<ExtendedEpisode>){
        if(items != newItems){
            items = newItems
            notifyDataSetChanged()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.episode_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ViewHolder).bind(items[position], clickListener)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(episode: ExtendedEpisode, listener: OnItemClickListener) = with(itemView) {
            tvName.text = episode.name
            ivPoster.contentDescription = episode.name

            if(episode.overview.isEmpty())
                tvOverview.visibility = View.GONE
            else{
                tvOverview.text = episode.overview
                tvOverview.visibility = View.VISIBLE
            }

            tvDate.text = resources.getString(R.string.format_episode_season_number, episode.ep_number, episode.seasonNumber)

            tvEpisodeInfo.text = episode.seriesName
            if(episode.seen){
                ivSeenIcon.setImageResource(R.drawable.eye)
            }else{
                ivSeenIcon.setImageResource(R.drawable.eye_off)
            }

            ivPoster.picasso(episode.poster)

            // RecyclerView on item click
            setOnClickListener {
                listener.onItemClick(episode, it)
            }
        }
    }
}