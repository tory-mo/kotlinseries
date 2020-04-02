package by.torymo.kotlinseries.data

import android.app.Application
import android.content.SharedPreferences
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.preference.PreferenceManager
import by.torymo.kotlinseries.data.db.ExtendedEpisode
import by.torymo.kotlinseries.data.db.Season
import by.torymo.kotlinseries.data.db.Series
import by.torymo.kotlinseries.data.network.Requester
import by.torymo.kotlinseries.data.network.SearchResponse
import by.torymo.kotlinseries.data.network.SeriesDetailsResponse
import by.torymo.kotlinseries.ui.fragment.SearchFragment
import by.torymo.kotlinseries.ui.fragment.SeriesDetailsFragment
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

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

    fun search(query: String, page: Int, callback: SearchFragment.SearchCallback){
        val call = seriesRequester.search(query, page)

        call.enqueue(object : Callback<SearchResponse> {
            override fun onResponse(call: Call<SearchResponse>?, response: Response<SearchResponse>?) {
                if (response != null && response.isSuccessful) {
                    seriesDbRepository.clearTemporary()
                    val searchResult = response.body()
                    searchResult?.results?.forEach {
                        seriesDbRepository.insertOrUpdateSeriesMainInfo(it)
                    }

                    callback.onSuccess(listOf())
                } else {
                    callback.onError(response?.message())
                }
            }

            override fun onFailure(call: Call<SearchResponse>?, t: Throwable?) {
                callback.onError(t?.message)
            }
        })
    }

    fun getSeriesByName(name: String): LiveData<List<Series>> = seriesDbRepository.getByName(name)

    fun seriesFollowingChanged(series: Series){
        if(series.temporary) {
            seriesDbRepository.startFollowingSeries(series.id)
            GlobalScope.launch { // launch a new coroutine in background and continue
                updateEpisodes(series.id)
            }

        }else {
            seriesDbRepository.stopFollowingSeries(series.id)
            seriesDbRepository.deleteEpisodesBySeries(series.id)
        }
    }

    @WorkerThread
    fun requestSeriesDetails(mdbId: Long, callback: SeriesDetailsFragment.DetailCallback){
        val call = seriesRequester.getSeriesDetails(mdbId)

        call.enqueue(object: Callback<SeriesDetailsResponse>{
            override fun onResponse(call: Call<SeriesDetailsResponse>, response: Response<SeriesDetailsResponse>) {
                if (response.isSuccessful) {
                    val seriesDetailsResult = response.body()
                    seriesDetailsResult?.let {
                        seriesDbRepository.updateSeriesDetails(it)

                        seriesDbRepository.insertOrUpdateSeasons(mdbId, it.seasons)
                    }
                } else {
                    callback.onError(response.message())
                }
            }

            override fun onFailure(call: Call<SeriesDetailsResponse>, t: Throwable) {
                callback.onError(t.message)
            }
        })
    }

    fun getSeriesByType(type: SeriesType): LiveData<List<Series>> = seriesDbRepository.getSeriesByType(type)

    fun requestSeries(page: Int, type: SeriesType){

        val call = when(type) {
            SeriesType.POPULAR -> seriesRequester.popular(page)
            else -> seriesRequester.airingToday(page)
        }

        call.enqueue(object: Callback<SearchResponse>{
                override fun onResponse(call: Call<SearchResponse>, response: Response<SearchResponse>) {
                    if(response.isSuccessful){
                        if(page == 1) seriesDbRepository.clearTemporary(type)
                        val searchResult = response.body()
                        searchResult?.results?.forEach {
                            seriesDbRepository.insertOrUpdateSeriesMainInfo(it, type)
                        }
                    }
                }

                override fun onFailure(call: Call<SearchResponse>, t: Throwable) {

                }
            })
    }

    fun getSeriesDetails(mdbId: Long): LiveData<Series> = seriesDbRepository.getSeries(mdbId)

    fun clearSearchResult() = seriesDbRepository.clearTemporary()

    @WorkerThread
    fun updateEpisodes(): Boolean{
        val seriesList = seriesDbRepository.getSeriesList()

        var updated = 0
        for(series: Series in seriesList){
            if(!series.temporary) updated += updateEpisodes(series.id)
        }

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
}
