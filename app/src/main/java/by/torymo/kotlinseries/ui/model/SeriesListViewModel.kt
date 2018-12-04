package by.torymo.kotlinseries.ui.model


import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MediatorLiveData
import by.torymo.kotlinseries.SeriesApp
import by.torymo.kotlinseries.data.db.Series
import by.torymo.kotlinseries.ui.fragment.SeriesFragment

class SeriesListViewModel(application: Application): AndroidViewModel(application) {
    private val seriesRepository = getApplication<SeriesApp>().getSeriesRepository()
    val seriesList = MediatorLiveData<List<Series>>()

    init {
        getAllSeries()
    }

    fun getAllSeries(){
        seriesList.addSource(seriesRepository.getSeriesList()){
            series->seriesList.postValue(series)
        }

    }

    fun getSearchResult(){
        seriesList.addSource(seriesRepository.getSearchResult()){
            series->seriesList.postValue(series)
        }
    }

    fun searchSeries(query: String, page: Int,  callback: SeriesFragment.SearchCallback){
        seriesRepository.search(query, page, callback)
    }

    fun clearSearch(){
        seriesRepository.clearSearchResult()
        getAllSeries()
    }
}