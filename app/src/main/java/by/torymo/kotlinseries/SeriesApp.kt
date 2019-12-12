package by.torymo.kotlinseries

import android.app.Application
import by.torymo.kotlinseries.data.SeriesRepository
import com.jakewharton.threetenabp.AndroidThreeTen

class SeriesApp: Application() {
    fun getSeriesRepository() = SeriesRepository(this)

    override fun onCreate() {
        super.onCreate()

        AndroidThreeTen.init(this)
    }
}