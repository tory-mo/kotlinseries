package by.torymo.kotlinseries

import android.app.Activity
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.InstallState
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability

class InAppUpdater constructor(private val stateChanged:(Int) -> Unit):InstallStateUpdatedListener {

    companion object{
        const val REQUEST_CODE_FLEXIBLE_UPDATE = 1789546;
    }

    private lateinit var appUpdateManager: AppUpdateManager

    fun init(context: Activity){
        appUpdateManager = AppUpdateManagerFactory.create(context)
        appUpdateManager.registerListener(this)
        checkUpdateAvailable(context)
    }
    
    fun checkUpdateAvailable(context: Activity){
        appUpdateManager.appUpdateInfo.addOnSuccessListener {
            appUpdateInfo ->
                if(appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                        && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)){
                    appUpdateManager.startUpdateFlowForResult(
                            appUpdateInfo,
                            AppUpdateType.FLEXIBLE,
                            context,
                            REQUEST_CODE_FLEXIBLE_UPDATE
                    )
                }else{
                    unregisterListener()
                }
        }
    }

    fun checkForUnfinishedUpdates(){
        appUpdateManager.appUpdateInfo.addOnSuccessListener { appUpdateInfo ->
            if(appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED)
                stateChanged.invoke(appUpdateInfo.installStatus())
        }
    }

    fun completeUpdate(){
        appUpdateManager.completeUpdate()
    }

    fun unregisterListener() {
        appUpdateManager.unregisterListener(this)
    }

    override fun onStateUpdate(state: InstallState) {
        if (state.installStatus() == InstallStatus.INSTALLED)
            unregisterListener()
        stateChanged.invoke(state.installStatus())
    }
}