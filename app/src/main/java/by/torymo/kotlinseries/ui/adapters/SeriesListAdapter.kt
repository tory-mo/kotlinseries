package by.torymo.kotlinseries.ui.adapters


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import by.torymo.kotlinseries.data.db.Series
import by.torymo.kotlinseries.R
import by.torymo.kotlinseries.picasso
import kotlinx.android.synthetic.main.series_item.view.*

class SeriesListAdapter(private val clickListener: OnItemClickListener):
PagingDataAdapter<Series, RecyclerView.ViewHolder>(DATA_COMPARATOR){

    companion object {
        private val DATA_COMPARATOR = object : DiffUtil.ItemCallback<Series>() {
            override fun areItemsTheSame(oldItem: Series, newItem: Series): Boolean =
                    oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Series, newItem: Series): Boolean =
                    oldItem == newItem
        }
    }

    interface OnItemClickListener{
        fun onItemClick(series: Series, item: View)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        if(item != null)
            (holder as ViewHolder).bind(item, clickListener)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        companion object{
            fun create(parent: ViewGroup): ViewHolder{
                val view = LayoutInflater.from(parent.context)
                        .inflate(R.layout.series_item, parent, false)
                return ViewHolder(view)
            }
        }

        fun bind(series: Series, listener: OnItemClickListener) = with(itemView) {
            tvName.text = series.name
            ivPoster.contentDescription = series.name
            tvDate.text = series.overview
            ivPoster.picasso(series.poster)
            tvVoteAvg.text = series.voteAverage.toString()

            // RecyclerView on item click
            setOnClickListener {
                listener.onItemClick(series, it)
            }
        }
    }
}