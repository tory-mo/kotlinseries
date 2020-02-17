package by.torymo.kotlinseries.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import by.torymo.kotlinseries.R
import by.torymo.kotlinseries.data.SeriesRepository
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.fragment_series_tab.*

class SeriesTabLayoutFragment: Fragment() {

    private var fragmentAdapter: SeriesPagerAdapter? = null
    private val titles = arrayOf(R.string.favourite, R.string.airing_today, R.string.popular)


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_series_tab, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        toolbarTitle.text = getString(R.string.series)

        activity?.let {
            fragmentAdapter = SeriesPagerAdapter(it)
            seriesViewPager.adapter = fragmentAdapter
            TabLayoutMediator(seriesTabLayout, seriesViewPager,
                    TabLayoutMediator.TabConfigurationStrategy { tab, position ->
                        tab.text = getString(titles[position])
                    }).attach()
        }
    }

    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity).setSupportActionBar(seriesToolbar)
    }

    class SeriesPagerAdapter(fm: FragmentActivity): FragmentStateAdapter(fm){
        private val fragments = arrayOf(SeriesFragment(),
                AiringTodayFragment.newInstance(SeriesRepository.Companion.SeriesType.AIRING_TODAY),
                AiringTodayFragment.newInstance(SeriesRepository.Companion.SeriesType.POPULAR))

        override fun getItemCount(): Int {
            return fragments.size
        }

        override fun createFragment(position: Int): Fragment {
            return fragments[position]
        }
    }
}