package by.torymo.kotlinseries.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import by.torymo.kotlinseries.InAppUpdater
import by.torymo.kotlinseries.R
import by.torymo.kotlinseries.ui.model.MainActivityViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.android.play.core.install.model.InstallStatus
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(), MainActivityViewModel.UpdatedCallback {

    private val SELECTED_KEY = "selected_item"
    private var selectedTab: Int = 0

    interface UpdateListener{
        fun onUpdated()
    }

    private var listener: UpdateListener? = null
    private lateinit var inAppUpdater: InAppUpdater

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

        inAppUpdater = InAppUpdater(::onStateUpdateChanged)
        inAppUpdater.init(this)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        bottomNavigation?.selectedItemId?.let {
            outState.putInt(SELECTED_KEY, bottomNavigation.selectedItemId)
        }
        super.onSaveInstanceState(outState)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == InAppUpdater.REQUEST_CODE_FLEXIBLE_UPDATE
                && resultCode != Activity.RESULT_OK)
            Snackbar.make(clMainContainer, "Update failed", Snackbar.LENGTH_LONG).show()
    }

    fun setListener(listener: UpdateListener){
        this.listener = listener
    }

    override fun onUpdated() {
        listener?.onUpdated()
    }

    override fun onResume() {
        super.onResume()
        inAppUpdater.checkForUnfinishedUpdates()
    }

    override fun onDestroy() {
        super.onDestroy()
        listener = null
        inAppUpdater.unregisterListener()
    }

    private fun onStateUpdateChanged(installStatus: Int){
        when(installStatus){
            InstallStatus.DOWNLOADED -> {
                val snackbar: Snackbar = Snackbar.make(
                        clMainContainer,
                        "Для завершения установки необходимо перезагрузить приложение",
                        Snackbar.LENGTH_INDEFINITE)

                snackbar.setAction("Перезагрузить") {
                    inAppUpdater.completeUpdate()
                }
                snackbar.show()
            }
        }
    }
}
