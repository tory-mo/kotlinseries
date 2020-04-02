package by.torymo.kotlinseries.ui.fragment

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.adapter.FragmentStateAdapter
import by.torymo.kotlinseries.R
import by.torymo.kotlinseries.data.SeriesRepository
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.fragment_series_tab.*

class SeriesTabLayoutFragment: Fragment() {

    private var fragmentAdapter: SeriesPagerAdapter? = null
    private val titles = arrayOf(R.string.favourite, R.string.airing_today, R.string.popular)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.clear()
        inflater.inflate(R.menu.series_list_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == R.id.action_search) {
            findNavController().navigate(SeriesTabLayoutFragmentDirections.toSearch())
            true
        } else {
            false
        }
    }

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
        private val fragments = arrayOf(SeriesListFragment.newInstance(SeriesRepository.Companion.SeriesType.WATCHLIST),
                SeriesListFragment.newInstance(SeriesRepository.Companion.SeriesType.AIRING_TODAY),
                SeriesListFragment.newInstance(SeriesRepository.Companion.SeriesType.POPULAR))

        override fun getItemCount(): Int {
            return fragments.size
        }

        override fun createFragment(position: Int): Fragment {
            return fragments[position]
        }
    }
}