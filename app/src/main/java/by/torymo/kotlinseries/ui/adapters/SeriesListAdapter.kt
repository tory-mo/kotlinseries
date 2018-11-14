package by.torymo.kotlinseries.ui.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import by.torymo.kotlinseries.data.db.Series
import by.torymo.kotlinseries.R
import kotlinx.android.synthetic.main.series_item.view.*

class SeriesListAdapter(private val items: List<Series>,
                        private val clickListener: OnItemClickListener):
RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    interface OnItemClickListener{
        fun onItemClick(series: Series, item: View)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.series_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ViewHolder).bind(items[position], clickListener)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(series: Series, listener: OnItemClickListener) = with(itemView) {
            tvName.text = series.name
            // RecyclerView on item click
            setOnClickListener {
                listener.onItemClick(series, it)
            }
        }
    }
}