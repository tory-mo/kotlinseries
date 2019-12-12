package by.torymo.kotlinseries.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import by.torymo.kotlinseries.DateTimeUtils
import by.torymo.kotlinseries.R
import by.torymo.kotlinseries.data.db.ExtendedEpisode
import by.torymo.kotlinseries.picasso
import kotlinx.android.synthetic.main.episode_item.view.*

class EpisodeListAdapter(private val clickListener: OnItemClickListener):
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var items: List<ExtendedEpisode> = listOf()

    interface OnItemClickListener{
        fun onItemClick(episode: ExtendedEpisode, item: View)
    }

    fun setItems(newItems: List<ExtendedEpisode>){
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

            if(episode.date != 0L)
                tvDate.text = DateTimeUtils.format(episode.date)
            else tvDate.text = ""


            tvEpisodeInfo.text = resources.getString(R.string.format_episode_season_number, episode.ep_number, episode.seasonNumber)
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