package by.torymo.kotlinseries

import android.app.Application
import by.torymo.kotlinseries.data.SeriesRepository

class SeriesApp: Application() {
    fun getSeriesRepository() = SeriesRepository(this)
}