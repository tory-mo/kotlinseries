package by.torymo.kotlinseries.ui.model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import by.torymo.kotlinseries.SeriesApp
import by.torymo.kotlinseries.data.db.Season
import by.torymo.kotlinseries.data.db.Series
import by.torymo.kotlinseries.data.network.Cast
import by.torymo.kotlinseries.data.network.Seasons
import by.torymo.kotlinseries.ui.fragment.SeriesDetailsFragment

class SeriesDetailsViewModel(application: Application): AndroidViewModel(application) {
    private val seriesRepository = getApplication<SeriesApp>().getSeriesRepository()
    private val seriesId = MutableLiveData<Long>()

    fun getSeriesById(mdbId: Long): LiveData<Series> {
        seriesId.value = mdbId

        return  Transformations.switchMap<Long, Series>(seriesId) { id ->
            seriesRepository.getSeriesDetails(id)
        }
    }

    suspend fun getSeriesDetails(mdbId: Long): Triple<Series, List<Season>?, List<Cast>?>{
        return seriesRepository.requestSeriesDetails(mdbId)
    }

    suspend fun checkFollowing(mdbId: Long): Series?{
        return seriesRepository.getSeries(mdbId)
    }

    fun seriesFollowingStatusChanged(series: Series, follow: Boolean){
        seriesRepository.seriesFollowingChanged(series, follow)
    }

    fun getSeasons(series: Long): LiveData<List<Season>>{
        return seriesRepository.getSeasons(series)
    }

    fun changeSeasonFollowing(season: Season, requestEpisodes: Boolean = false){
        seriesRepository.changeSeasonFollowing(season, requestEpisodes)
    }
}