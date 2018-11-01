package by.torymo.kotlinseries.ui

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MediatorLiveData
import by.torymo.kotlinseries.SeriesApp
import by.torymo.kotlinseries.domain.Series

class SeriesListViewModel(application: Application): AndroidViewModel(application) {
    private val seriesDbRepository = getApplication<SeriesApp>().getSeriesRepository()
    val seriesList = MediatorLiveData<List<Series>>()

    init {
        getAllSeries()
    }

    fun getAllSeries(){
        seriesList.addSource(seriesDbRepository.getAllSeries()){
            series->seriesList.postValue(series)
        }
    }

    fun getSeriesList(): LiveData<List<Series>> {
        return seriesList
    }
}