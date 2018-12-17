package by.torymo.kotlinseries.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import by.torymo.kotlinseries.R
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val SELECTED_KEY = "selected_item"
    private var selectedTab: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navController = Navigation.findNavController(this, R.id.bottomNavFragment)


        if(savedInstanceState != null && savedInstanceState.containsKey(SELECTED_KEY)){
            selectedTab = savedInstanceState.getInt(SELECTED_KEY)
            bottomNavigation.selectedItemId = selectedTab
        }
        NavigationUI.setupWithNavController(bottomNavigation,
                navController)

    }

    override fun onSaveInstanceState(outState: Bundle) {
        bottomNavigation?.selectedItemId?.let {
            outState.putInt(SELECTED_KEY, bottomNavigation.selectedItemId)
        }
        super.onSaveInstanceState(outState)
    }
}
