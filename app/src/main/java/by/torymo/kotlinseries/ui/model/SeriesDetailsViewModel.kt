package by.torymo.kotlinseries.ui.model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import by.torymo.kotlinseries.SeriesApp
import by.torymo.kotlinseries.data.db.Season
import by.torymo.kotlinseries.data.db.Series
import by.torymo.kotlinseries.ui.DetailActivity.DetailCallback

class SeriesDetailsViewModel(application: Application): AndroidViewModel(application) {
    private val seriesRepository = getApplication<SeriesApp>().getSeriesRepository()
    private val seriesId = MutableLiveData<Long>()

    fun getSeriesById(mdbId: Long): LiveData<Series> {
        seriesId.value = mdbId

        return  Transformations.switchMap<Long, Series>(seriesId) { id ->
            seriesRepository.getSeriesDetails(id)
        }
    }

    fun getSeriesDetails(mdbId: Long, callback: DetailCallback){
        seriesRepository.requestSeriesDetails(mdbId, callback)
    }

    fun seriesFollowingStatusChanged(series: Series){
        seriesRepository.seriesFollowingChanged(series)
    }

    fun getSeasons(series: Long): LiveData<List<Season>>{
        return seriesRepository.getSeasons(series)
    }

    fun changeSeasonFollowing(season: Season, requestEpisodes: Boolean = false){
        seriesRepository.changeSeasonFollowing(season, requestEpisodes)
    }
}