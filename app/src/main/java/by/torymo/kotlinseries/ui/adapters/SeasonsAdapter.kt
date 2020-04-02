package by.torymo.kotlinseries.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import by.torymo.kotlinseries.DateTimeUtils
import by.torymo.kotlinseries.R
import by.torymo.kotlinseries.data.db.Season
import by.torymo.kotlinseries.picasso
import kotlinx.android.synthetic.main.season_item.view.*


class SeasonsAdapter(private val clickListener: OnItemClickListener):
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    interface OnItemClickListener{
        fun onItemClick(season: Season, item: View)
        fun onItemMenuClick(season: Season, item: View)
    }

    private var items: List<Season> = listOf()
    private var showCheckBox: Boolean = false

    fun setCheckbox(showCheckBox: Boolean){
        if(this.showCheckBox != showCheckBox){
            this.showCheckBox = showCheckBox
        }
    }

    fun setItems(newItems: List<Season>, showCheckBox: Boolean){
        val diffCallback = SeasonsDiffCallback(items, newItems)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        items = newItems
        diffResult.dispatchUpdatesTo(this)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.season_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ViewHolder).bind(items[position], clickListener, showCheckBox)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(season: Season, listener: OnItemClickListener, showCheckBox: Boolean) = with(itemView) {
            tvName.text = season.name
            tvEpisodes.text = when (season.episodes) {
                1 -> context.getString(R.string.format_one_episode, season.episodes)
                in 2..4 -> context.getString(R.string.format_two_episodes, season.episodes)
                else -> context.getString(R.string.format_many_episodes, season.episodes)
            }

            if(season.overview.isEmpty())
                tvOverview.visibility = View.GONE
            else{
                tvOverview.text = season.overview
                tvOverview.visibility = View.VISIBLE
            }

            if(season.date != 0L)
                tvDate.text = DateTimeUtils.format(season.date)
            else tvDate.text = ""

            ivPoster.picasso(season.poster)

            if(showCheckBox) {
                if(season.following){
                    isEnabled = true
                    ivFollowing.setImageResource(R.drawable.ic_favorite)
                }
                else {
                    ivFollowing.setImageResource(R.drawable.ic_not_favorite)
                    isEnabled = false
                }

                setOnClickListener {
                    listener.onItemClick(season, it)
                }

                ivFollowing.setOnClickListener {
                    if(!season.following){
                        isEnabled = true
                        ivFollowing.setImageResource(R.drawable.ic_favorite)
                    }
                    else {
                        isEnabled = false
                        ivFollowing.setImageResource(R.drawable.ic_not_favorite)
                    }

                    listener.onItemMenuClick(season, it)
                }
            }else{
                ivFollowing.visibility = View.GONE
                isEnabled = false
            }


        }
    }

    class SeasonsDiffCallback(private val oldSeasons: List<Season>, private val newSeasons: List<Season>): DiffUtil.Callback() {

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean = oldSeasons[oldItemPosition].id == newSeasons[newItemPosition].id

        override fun getOldListSize(): Int  = oldSeasons.size

        override fun getNewListSize(): Int = newSeasons.size

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldSeason = oldSeasons[oldItemPosition]
            val newSeason = newSeasons[newItemPosition]

            return (oldSeason.episodes == newSeason.episodes) && (oldSeason.number == newSeason.number) && (oldSeason.following == newSeason.following)
                    && (oldSeason.date == newSeason.date) && (oldSeason.number == newSeason.number) && (oldSeason.name == newSeason.name) && (oldSeason.overview == newSeason.overview)
                    && (oldSeason.poster == newSeason.poster) && (oldSeason.series == newSeason.series)
        }
    }
}