package by.torymo.kotlinseries


import android.content.Context
import android.preference.PreferenceManager
import java.text.SimpleDateFormat


class Utility {
    val POSTER_PATH = "http://image.tmdb.org/t/p/w300/"

    private val PREF_SEEN = "pref_seen"
    val dateToStrFormat = SimpleDateFormat("dd MMMM yyyy")

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
}