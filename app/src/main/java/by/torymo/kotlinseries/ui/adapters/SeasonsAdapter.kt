package by.torymo.kotlinseries.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
    }

    private var items: List<Season> = listOf()
    private var showCheckBox: Boolean = false

    fun setCheckbox(showCheckBox: Boolean){
        if(this.showCheckBox != showCheckBox){
            this.showCheckBox = showCheckBox
            notifyDataSetChanged()
        }
    }

    fun setItems(newItems: List<Season>, showCheckBox: Boolean){
        val notify = items != newItems || this.showCheckBox != showCheckBox

        this.showCheckBox = showCheckBox
        if(items != newItems){
            items = newItems

        }

        if(notify) notifyDataSetChanged()
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

            if(showCheckBox) {
                cbFollowing.isChecked = season.following
                cbFollowing.visibility = View.VISIBLE
            }else{
                cbFollowing.visibility = View.GONE
            }

            ivPoster.picasso(season.poster)

            setOnClickListener {
                listener.onItemClick(season, it)
                cbFollowing.isChecked = !cbFollowing.isChecked
            }
        }
    }
}