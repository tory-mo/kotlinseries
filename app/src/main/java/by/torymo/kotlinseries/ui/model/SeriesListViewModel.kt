package by.torymo.kotlinseries.ui.model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.paging.PagingData
import by.torymo.kotlinseries.SeriesApp
import by.torymo.kotlinseries.data.SeriesRepository
import by.torymo.kotlinseries.data.db.Series
import kotlinx.coroutines.flow.Flow

class SeriesListViewModel(application: Application): AndroidViewModel(application) {

    private val seriesRepository = getApplication<SeriesApp>().getSeriesRepository()

    fun seriesList(type: SeriesRepository.Companion.SeriesType): Flow<PagingData<Series>> = seriesRepository.getSeries(type)
}