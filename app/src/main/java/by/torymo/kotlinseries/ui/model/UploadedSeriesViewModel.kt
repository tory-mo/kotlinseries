package by.torymo.kotlinseries.ui.model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import by.torymo.kotlinseries.SeriesApp
import by.torymo.kotlinseries.data.SeriesRepository
import by.torymo.kotlinseries.data.db.Series

class UploadedSeriesViewModel(application: Application): AndroidViewModel(application) {

    private val seriesRepository = getApplication<SeriesApp>().getSeriesRepository()

    fun seriesList(type: SeriesRepository.Companion.SeriesType): LiveData<List<Series>> = seriesRepository.getSeriesByType(type)
}