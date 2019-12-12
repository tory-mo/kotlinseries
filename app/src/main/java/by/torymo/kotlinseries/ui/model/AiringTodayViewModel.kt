package by.torymo.kotlinseries.ui.model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import by.torymo.kotlinseries.SeriesApp
import by.torymo.kotlinseries.data.db.Series

class AiringTodayViewModel(application: Application): AndroidViewModel(application) {

    private val seriesRepository = getApplication<SeriesApp>().getSeriesRepository()

    val seriesList: LiveData<List<Series>> = seriesRepository.getAiringTodaySeries()


}