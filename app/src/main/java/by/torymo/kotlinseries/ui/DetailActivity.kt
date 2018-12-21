package by.torymo.kotlinseries.ui

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import by.torymo.kotlinseries.R
import by.torymo.kotlinseries.data.db.Series
import by.torymo.kotlinseries.picasso
import by.torymo.kotlinseries.ui.fragment.EpisodesFragment
import by.torymo.kotlinseries.ui.fragment.OverviewFragment
import by.torymo.kotlinseries.ui.model.SeriesDetailsViewModel
import com.google.android.material.appbar.CollapsingToolbarLayout
import kotlinx.android.synthetic.main.activity_detail.*
import android.content.res.ColorStateList
import androidx.core.view.ViewCompat.setBackgroundTintList



class DetailActivity : AppCompatActivity() {

    private lateinit var viewModel: SeriesDetailsViewModel
    private lateinit var seriesUpdatedCallback: SeriesUpdatedCallback
    private lateinit var collapsingToolbarLayout: CollapsingToolbarLayout

    companion object {
        public val SERIES_EXTRA = "series_extra"
    }

    public fun setUpdatedCallback(clbk: SeriesUpdatedCallback){
        seriesUpdatedCallback = clbk
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        //NavigationUI.setupWithNavController(supportActionBar, navController)

        val seriesId = DetailActivityArgs.fromBundle(intent.extras).seriesId

        viewModel = ViewModelProviders.of(this).get(SeriesDetailsViewModel::class.java)

        viewModel.getSeriesById(seriesId).observe(this, Observer<Series>{ series ->
            series?.let { refreshSeries(series) }
        })
        viewModel.getSeriesDetails(seriesId, object : DetailCallback{
            override fun onError(message: String?) {
                Toast.makeText(application, message, Toast.LENGTH_LONG).show()
            }
        })

        setSupportActionBar(findViewById<Toolbar>(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        collapsingToolbarLayout = findViewById<CollapsingToolbarLayout>(R.id.collapsing_toolbar)
    }

    private fun refreshSeries(series: Series){
        ep_view_pager.adapter = DetailsPagerAdapter(supportFragmentManager, if(series.temporary) 1 else 2, series, this)

        tab_layout.setupWithViewPager(ep_view_pager)
        fbFavourite.setImageDrawable(getDrawable(if(series.temporary) R.drawable.ic_not_favorite else R.drawable.ic_favorite))

        collapsingToolbarLayout.title = series.name
        title = series.name
        tvSeriesOriginalName.text = series.originalName

        ivEpisodesHeader.picasso(series.backdrop)
        //seriesUpdatedCallback?.onUpdated(series)
    }


    class DetailsPagerAdapter(fragmentManager: FragmentManager, private val count: Int, private val series: Series, private val context: Context): FragmentPagerAdapter(fragmentManager) {

        private val TITLES = arrayOf(R.string.overview, R.string.episodes)

        override fun getCount(): Int {
            return count
        }

        override fun getItem(position: Int): Fragment? {
            return when(position){
                0 -> OverviewFragment.newInstance(series)
                1 -> {
                    val ef = EpisodesFragment()
                    ef
                }
                else -> {
                    null
                }
            }
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return context.getString(TITLES[position])
        }
    }

    interface DetailCallback{
        fun onError(message: String?)
    }

    interface SeriesUpdatedCallback{
        fun onUpdated(series: Series)
    }
}
