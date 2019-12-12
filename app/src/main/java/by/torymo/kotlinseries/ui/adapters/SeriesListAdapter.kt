package by.torymo.kotlinseries.ui.adapters


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import by.torymo.kotlinseries.data.db.Series
import by.torymo.kotlinseries.R
import by.torymo.kotlinseries.picasso
import kotlinx.android.synthetic.main.series_item.view.*

class SeriesListAdapter(private val clickListener: OnItemClickListener):
RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    private var items: List<Series> = listOf()

    interface OnItemClickListener{
        fun onItemClick(series: Series, item: View)
    }

    fun setItems(newItems: List<Series>){
        if(items != newItems){
            items = newItems
            notifyDataSetChanged()
        }
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
            ivPoster.contentDescription = series.name
            tvDate.text = series.overview
            ivPoster.picasso(series.poster)

            // RecyclerView on item click
            setOnClickListener {
                listener.onItemClick(series, it)
            }
        }
    }
}