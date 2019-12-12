package by.torymo.kotlinseries.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager2.adapter.FragmentStateAdapter
import by.torymo.kotlinseries.R
import by.torymo.kotlinseries.data.db.Season
import by.torymo.kotlinseries.data.db.Series
import by.torymo.kotlinseries.picasso
import by.torymo.kotlinseries.ui.adapters.SeasonsAdapter
import by.torymo.kotlinseries.ui.fragment.EpisodesFragment
import by.torymo.kotlinseries.ui.fragment.OverviewFragment
import by.torymo.kotlinseries.ui.model.SeriesDetailsViewModel
import kotlinx.android.synthetic.main.activity_detail.*
import com.google.android.material.tabs.TabLayoutMediator

class DetailActivity : AppCompatActivity(), SeasonsAdapter.OnItemClickListener {

    private lateinit var viewModel: SeriesDetailsViewModel

    private var seriesId: Long = 0
    private var currSeries: Series? = null

    private var adapter: DetailsPagerAdapter? = null
    private val titles = arrayOf(R.string.overview, R.string.episodes)


    override fun onItemClick(season: Season, item: View) {
        val tmpSeries = currSeries
        viewModel.changeSeasonFollowing(season, if(tmpSeries == null) false else !tmpSeries.temporary)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        adapter = DetailsPagerAdapter(this)
        detailsViewPager.adapter = adapter

        TabLayoutMediator(tab_layout, detailsViewPager,
                TabLayoutMediator.TabConfigurationStrategy { tab, position ->
                    tab.text = getString(titles[position])
                }).attach()


        seriesId = DetailActivityArgs.fromBundle(intent.extras).seriesId

        viewModel = ViewModelProviders.of(this).get(SeriesDetailsViewModel::class.java)
        viewModel.getSeriesById(seriesId).observe(this, Observer<Series>{ series ->
            series?.let {
                refreshSeries(series)
            }
        })
        viewModel.getSeriesDetails(seriesId, object : DetailCallback{
            override fun onError(message: String?) {
                Toast.makeText(application, message, Toast.LENGTH_LONG).show()
            }
        })
        viewModel.getSeasons(seriesId).observe(this, Observer<List<Season>> {
            it?.let {
                val tmpSeries = currSeries
                adapter?.updateSeasonsInfo(it, if(tmpSeries != null) !tmpSeries.temporary else false)
            }
        })

        fbFavourite.setOnClickListener{
            favouriteStatusChanged()
        }
    }

    private fun refreshSeries(series: Series){
        currSeries = series

        adapter?.update(if(series.temporary) 1 else 2, series)

        fbFavourite.setImageDrawable(getDrawable(if(series.temporary) R.drawable.ic_not_favorite else R.drawable.ic_favorite))

        collapsing_toolbar?.title = series.name
        title = series.name
        tvSeriesOriginalName.text = series.originalName

        ivEpisodesHeader.picasso(series.backdrop)
    }

    private fun favouriteStatusChanged(){
        val series = currSeries ?: return

        viewModel.seriesFollowingStatusChanged(series)
        adapter?.updateSeasonsInfo(series.temporary)
        fbFavourite.setImageDrawable(getDrawable(if(!series.temporary) R.drawable.ic_not_favorite else R.drawable.ic_favorite))
    }

    class DetailsPagerAdapter(fragmentManager: FragmentActivity): FragmentStateAdapter(fragmentManager) {

        private var count = 0
        private val fragments: MutableList<Fragment> = mutableListOf()


        fun update(count: Int, series: Series){
            if(fragments.count() != count){
                if(fragments.count() == 0)
                    fragments.add(OverviewFragment.newInstance(series))
                if(fragments.count() == 2 && count == 1)
                    fragments.removeAt(1)
                else if(count == 2)
                    fragments.add(EpisodesFragment.newInstance(series.id))
            }
            this.count = count
            (fragments[0] as OverviewFragment).fillInData(series)
            notifyDataSetChanged()
        }

        fun updateSeasonsInfo(seasons: List<Season>, persistentSeries: Boolean){
            if(fragments.size > 0 && fragments[0] is OverviewFragment){
                (fragments[0] as OverviewFragment).updateSeason(seasons, persistentSeries)
            }
        }

        fun updateSeasonsInfo(persistentSeries: Boolean){
            if(fragments.size > 0 && fragments[0] is OverviewFragment){
                (fragments[0] as OverviewFragment).updateSeason(persistentSeries)
            }
        }

        override fun getItemCount(): Int {
            return count
        }

        override fun createFragment(position: Int): Fragment {
            return fragments[position]
        }
    }

    interface DetailCallback{
        fun onError(message: String?)
    }
}
