package by.torymo.kotlinseries.data

import android.app.Application
import android.content.SharedPreferences
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.preference.PreferenceManager
import by.torymo.kotlinseries.data.db.ExtendedEpisode
import by.torymo.kotlinseries.data.db.Season
import by.torymo.kotlinseries.data.db.Series
import by.torymo.kotlinseries.data.network.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow

class SeriesRepository(application: Application){

    private val seriesDbRepository: SeriesDbRepository by lazy{
        SeriesDbRepository(application)
    }

    private val seriesPreferences: SharedPreferences by lazy{
        PreferenceManager.getDefaultSharedPreferences(application)
    }

    private val seriesRequester: Requester by lazy {
        Requester()
    }

    companion object {
        enum class EpisodeStatus {
            NOT_SEEN, ALL
        }

        // watchlist - 0, search result - 1, airing today - 2
        enum class SeriesType(val type: Int) {
            WATCHLIST(0),
            SEARCH_RESULT(1),
            AIRING_TODAY(2),
            POPULAR(3)
        }

        private const val PREF_SEEN = "pref_seen"
    }

    fun getSeenParam(): Boolean {
        return seriesPreferences.getBoolean(PREF_SEEN, false)
    }

    fun getSeenStatus(): EpisodeStatus{
        return  if(getSeenParam()) EpisodeStatus.NOT_SEEN
        else EpisodeStatus.ALL
    }

    fun changeSeenParam() {
        val old = seriesPreferences.getBoolean(PREF_SEEN, false)
        val editor = seriesPreferences.edit()
        editor.putBoolean(PREF_SEEN, !old)
        editor.apply()
    }

    fun getEpisodesForDay(date: Long, flag: EpisodeStatus = EpisodeStatus.ALL): List<ExtendedEpisode> {
        return seriesDbRepository.getEpisodesForDate(date, flag)
    }

    fun getEpisodeDatesBetweenDates(dateFrom: Long, dateTo: Long, flag: EpisodeStatus = EpisodeStatus.ALL): List<Long>{
        return seriesDbRepository.getEpisodeDatesBetweenDates(dateFrom, dateTo, flag)
    }

    private fun getEpisodesForSeries(series: Long): LiveData<List<ExtendedEpisode>> = seriesDbRepository.getEpisodesBySeries(series)
    fun getEpisodesForSeason(season: Long): LiveData<List<ExtendedEpisode>> = seriesDbRepository.getEpisodesBySeason(season)

    fun changeEpisodeSeen(id: Long, seen: Boolean = false) = seriesDbRepository.setSeen(id, seen)

    fun search(query: String): Flow<PagingData<Series>>{
        return Pager(
                config = PagingConfig(pageSize = 20, enablePlaceholders = false),
                pagingSourceFactory = {SearchPagingSource(query, seriesRequester)}
        ).flow
    }

    fun getSeriesByName(name: String): LiveData<List<Series>> = seriesDbRepository.getByName(name)

    suspend fun requestSeriesDetails(mdbId: Long): Triple<Series, List<Season>?, List<Cast>?>{
        val response = seriesRequester.getSeriesDetails(mdbId)
        val series = SeriesDetailsResponse.toSeries(response)

        val checkSeries = seriesDbRepository.getSeries(mdbId)
        var seasons: List<Season>? = Seasons.toSeason(series.id, response.seasons)
        if(checkSeries != null){
            seriesDbRepository.updateSeriesDetails(response)

            val seasonsT = response.seasons?.let {
                seriesDbRepository.insertOrUpdateSeasons(mdbId, it)
            }
            seasons = seasonsT ?: seasons
        }

        return Triple(series, seasons, response.aggregate_credits?.cast)
    }

    fun getSeries(type: SeriesType): Flow<PagingData<Series>>{
        var pageSize = 20
        val pagingSource = when(type) {
            SeriesType.WATCHLIST -> {
                pageSize = 100
                FavouritePagingSource(seriesDbRepository)
            }
            SeriesType.POPULAR -> PopularPagingSource(seriesRequester)
            else -> MdbPagingSource(seriesRequester)
        }

        return Pager(
                config = PagingConfig(pageSize = pageSize, enablePlaceholders = false),
                pagingSourceFactory = {pagingSource}
        ).flow
    }

    fun getSeriesDetails(mdbId: Long): LiveData<Series> = seriesDbRepository.getSeriesLiveData(mdbId)
    fun getSeries(mdbId: Long): Series? = seriesDbRepository.getSeries(mdbId)


    @WorkerThread
    fun updateEpisodes(): Boolean{
        val seriesList = seriesDbRepository.getSeriesList()

        var updated = 0
        /*for(series: Series in seriesList){
            if(!series.temporary) updated += updateEpisodes(series.id)
        }*/

        return updated > 0
    }

    @WorkerThread
    fun updateEpisodes(series: Long): Int{
        val seasons = seriesDbRepository.getFollowingOrLast(series)

        var updated = 0
        for(season: Season in seasons) {
            val seasonDetailsResult = seriesRequester.getSeasonDetails(series, season.number)
            seasonDetailsResult?.episodes?.forEach {
                seriesDbRepository.insertOrUpdateEpisode(series, season.id, it)
            }
            updated += seasonDetailsResult?.episodes?.size ?: 0
        }

        return updated
    }

    fun getSeasons(series: Long): LiveData<List<Season>> = seriesDbRepository.getSeasons(series)

    fun changeSeasonFollowing(season: Season, requestEpisodes: Boolean){
        season.following = !season.following

        seriesDbRepository.updateSeasonFollowing(season.id, season.following)
        if(season.following && requestEpisodes)
            GlobalScope.launch { // launch a new coroutine in background and continue
                updateEpisodes(season.series)
            }
        else
            seriesDbRepository.deleteEpisodesBySeason(season.id)
    }

    fun seriesFollowingChanged(series: Series, follow: Boolean){
        if(follow){
            seriesDbRepository.addSeries(series)
            GlobalScope.launch { // launch a new coroutine in background and continue
                updateEpisodes(series.id)
            }
        }else{
            seriesDbRepository.deleteSeries(series.id)
        }
    }
}
