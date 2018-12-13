package by.torymo.kotlinseries.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import by.torymo.kotlinseries.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val SELECTED_KEY = "selected_item"
    private var selectedTab: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navController = Navigation.findNavController(this, R.id.bottomNavFragment)

//        if(savedInstanceState!=null && savedInstanceState.containsKey(SELECTED_KEY)){
//            selectedTab = savedInstanceState.getInt(SELECTED_KEY)
//            bottomNavigation.selectedItemId = selectedTab
//        }

        NavigationUI.setupWithNavController(findViewById<BottomNavigationView>(R.id.bottomNavigation),
                navController)
    }

//    override fun onSaveInstanceState(outState: Bundle) {
//        outState.putInt(SELECTED_KEY, bottomNavigation?.selectedItemId)
//    }
}
