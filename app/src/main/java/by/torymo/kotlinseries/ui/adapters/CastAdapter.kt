package by.torymo.kotlinseries.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import by.torymo.kotlinseries.R
import by.torymo.kotlinseries.data.db.Season
import by.torymo.kotlinseries.data.db.Series
import by.torymo.kotlinseries.data.network.Cast
import by.torymo.kotlinseries.picasso
import kotlinx.android.synthetic.main.season_item.view.*

class CastAdapter(): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var items: List<Cast> = listOf()

    fun setItems(newItems: List<Cast>){
        val diffCallback = CastDiffCallback(items, newItems)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        items = newItems
        diffResult.dispatchUpdatesTo(this)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder.create(parent)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = items[position]
        (holder as ViewHolder).bind(item)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        companion object {
            fun create(parent: ViewGroup): ViewHolder {
                val view = LayoutInflater.from(parent.context)
                        .inflate(R.layout.cast_item, parent, false)
                return ViewHolder(view)
            }
        }

        fun bind(cast: Cast) = with(itemView) {
            tvName.text = cast.name
            ivPoster.picasso(cast.profile_path)
        }
    }

    class CastDiffCallback(private val oldCast: List<Cast>, private val newCast: List<Cast>): DiffUtil.Callback() {

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean = oldCast[oldItemPosition].id == newCast[newItemPosition].id

        override fun getOldListSize(): Int  = oldCast.size

        override fun getNewListSize(): Int = newCast.size

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldCast = oldCast[oldItemPosition]
            val newCast = newCast[newItemPosition]

            return (oldCast.id == newCast.id && oldCast.name == newCast.name && oldCast.profile_path == newCast.profile_path && oldCast.total_episodes_count == newCast.total_episodes_count)
        }
    }
}