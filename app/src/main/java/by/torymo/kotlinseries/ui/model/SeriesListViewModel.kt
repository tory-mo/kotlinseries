package by.torymo.kotlinseries.ui.model

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MediatorLiveData
import by.torymo.kotlinseries.SeriesApp
import by.torymo.kotlinseries.data.db.Series

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

    fun getSeriesList(): LiveData<List<Series>> {
        return seriesList
    }
}