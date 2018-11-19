package by.torymo.kotlinseries.ui.model


import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
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