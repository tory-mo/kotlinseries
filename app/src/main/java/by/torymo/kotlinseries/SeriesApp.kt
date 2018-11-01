package by.torymo.kotlinseries

import android.app.Application
import by.torymo.kotlinseries.data.SeriesDbRepository

class SeriesApp: Application() {
    fun getSeriesRepository() = SeriesDbRepository(this)
}