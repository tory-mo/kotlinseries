package by.torymo.kotlinseries.ui.model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import by.torymo.kotlinseries.SeriesApp
import by.torymo.kotlinseries.data.db.Series
import by.torymo.kotlinseries.ui.DetailActivity.DetailCallback
import by.torymo.kotlinseries.ui.fragment.DetailFragment

class SeriesDetailsViewModel(application: Application): AndroidViewModel(application) {
    private val seriesRepository = getApplication<SeriesApp>().getSeriesRepository()
    private val seriesId = MutableLiveData<String>()

    fun getSeriesById(mdbId: String): LiveData<Series> {
        seriesId.value = mdbId

        return  Transformations.switchMap<String, Series>(seriesId) { id ->
            seriesRepository.getSeriesDetails(id)
        }
    }

    fun getSeriesDetails(mdbId: String, callback: DetailCallback){
        seriesRepository.requestSeriesDetails(mdbId, callback)
    }

    fun getSeriesDetailsFr(mdbId: String, callback: DetailFragment.DetailCallback){
        seriesRepository.requestSeriesDetailsFr(mdbId, callback)
    }
}