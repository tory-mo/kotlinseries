package by.torymo.kotlinseries.ui.model

import android.app.Application
import androidx.lifecycle.*
import androidx.paging.PagingData
import by.torymo.kotlinseries.SeriesApp
import by.torymo.kotlinseries.data.db.Series
import kotlinx.coroutines.flow.Flow

class SearchViewModel(application: Application): AndroidViewModel(application) {

    private val seriesRepository = getApplication<SeriesApp>().getSeriesRepository()
    private val queryString = MutableLiveData<String>()

    fun searchSeries(query: String): Flow<PagingData<Series>> {
        queryString.value = query
        return seriesRepository.search(query)
    }

    fun clearSearch(){
        queryString.value = ""
    }
}