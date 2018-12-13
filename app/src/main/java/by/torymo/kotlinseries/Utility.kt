package by.torymo.kotlinseries


import android.content.Context
import android.preference.PreferenceManager
import java.text.SimpleDateFormat


class Utility {


    private val PREF_SEEN = "pref_seen"

    fun getSeenParam(context: Context?): Boolean {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        return prefs.getBoolean(PREF_SEEN, false)
    }

    fun changeSeenParam(context: Context?) {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        val old = prefs.getBoolean(PREF_SEEN, false)
        val editor = prefs.edit()
        editor.putBoolean(PREF_SEEN, !old)
        editor.apply()
    }

    companion object {
        val dateToStrFormat = SimpleDateFormat("dd MMMM yyyy")
    }
}