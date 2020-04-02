package by.torymo.kotlinseries.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import by.torymo.kotlinseries.R
import by.torymo.kotlinseries.ui.model.MainActivityViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), MainActivityViewModel.UpdatedCallback {

    private val SELECTED_KEY = "selected_item"
    private var selectedTab: Int = 0

    interface UpdateListener{
        fun onUpdated()
    }

    private var listener: UpdateListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if(savedInstanceState != null && savedInstanceState.containsKey(SELECTED_KEY)){
            selectedTab = savedInstanceState.getInt(SELECTED_KEY)
            bottomNavigation.selectedItemId = selectedTab
        }

        val navView: BottomNavigationView = findViewById(R.id.bottomNavigation)
        val navController = findNavController(R.id.bottomNavFragment)
        navView.setupWithNavController(navController)

        val viewModel = ViewModelProvider(this).get(MainActivityViewModel::class.java)
        viewModel.setCallback(this)
        viewModel.updateEpisodes()
        viewModel.requestAiringToday(1)
        viewModel.requestPopularSeries(1)

    }

    override fun onSaveInstanceState(outState: Bundle) {
        bottomNavigation?.selectedItemId?.let {
            outState.putInt(SELECTED_KEY, bottomNavigation.selectedItemId)
        }
        super.onSaveInstanceState(outState)
    }

    fun setListener(listener: UpdateListener){
        this.listener = listener
    }

    override fun onUpdated() {
        listener?.onUpdated()
    }

    override fun onDestroy() {
        super.onDestroy()
        listener = null
    }
}
