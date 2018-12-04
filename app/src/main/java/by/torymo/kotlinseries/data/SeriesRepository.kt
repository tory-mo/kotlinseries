package by.torymo.kotlinseries.data


import android.app.Application
import androidx.lifecycle.LiveData
import by.torymo.kotlinseries.data.db.Episode
import by.torymo.kotlinseries.data.db.Series
import by.torymo.kotlinseries.data.network.Requester
import by.torymo.kotlinseries.data.network.SearchResponse
import by.torymo.kotlinseries.ui.fragment.SeriesFragment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SeriesRepository(application: Application){

    private val seriesDbRepository = SeriesDbRepository(application)
    private val seriesRequester = Requester()
    companion object {
        enum class EpisodeStatus {
            SEEN, NOT_SEEN, ALL
        }
    }

    fun getEpisodesForDay(date: Long, flag: EpisodeStatus = EpisodeStatus.ALL): LiveData<List<Episode>> {
        return getEpisodesBetweenDates(date, date, flag)
    }

    fun getEpisodesBetweenDates(dateFrom: Long, dateTo: Long, flag: EpisodeStatus = EpisodeStatus.ALL): LiveData<List<Episode>>{
        return when(flag){
            EpisodeStatus.NOT_SEEN -> seriesDbRepository.getNotSeenEpisodesBetweenDates(dateFrom, dateTo)
            else -> seriesDbRepository.getEpisodesBetweenDates(dateFrom, dateTo)
        }
    }

    fun getEpisodeDatesBetweenDates(dateFrom: Long, dateTo: Long, flag: EpisodeStatus = EpisodeStatus.ALL): LiveData<List<Long>>{
        return when(flag){
            EpisodeStatus.NOT_SEEN -> seriesDbRepository.getNotSeenEpisodeDatesBetweenDates(dateFrom, dateTo)
            else -> seriesDbRepository.getEpisodeDatesBetweenDates(dateFrom, dateTo)
        }
    }

    fun getEpisodesForSeries(series: String): LiveData<List<Episode>>{
        return seriesDbRepository.getEpisodesBySeries(series)
    }

    fun changeEpisodeSeen(id: Long?, seen: EpisodeStatus = EpisodeStatus.NOT_SEEN){
        if(id != null)
            seriesDbRepository.setSeen(id, (seen == EpisodeStatus.NOT_SEEN))
    }

    fun search(query: String, page: Int, callback: SeriesFragment.SearchCallback){
        val call = seriesRequester.search(query, page)

        call.enqueue(object : Callback<SearchResponse> {
            override fun onResponse(call: Call<SearchResponse>?, response: Response<SearchResponse>?) {
                if (response != null && response.isSuccessful) {
                    seriesDbRepository.clearTemporary()
                    val searchResult = response.body()
                    searchResult?.results?.forEach { seriesDbRepository.insertSeries(it.toSeries()) }

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

    fun getSearchResult(): LiveData<List<Series>>{
        return seriesDbRepository.getSearchResult()
    }


    fun startFollowingSeries(mdbId: String){
        seriesDbRepository.startFollowingSeries(mdbId)
    }

    fun requestSeriesDetails(mdbId: String){
        val seriesDetailsResult = seriesRequester.getSeriesDetails(mdbId)

        seriesDetailsResult?.let {
            seriesDbRepository.updateSeriesDetails(mdbId, it.genres.toString(),it.homepage,it.number_of_seasons, it.status)
        }
    }

    fun getSeriesDetails(mdbId: String): LiveData<Series>{
        return seriesDbRepository.getSeriesByMdbId(mdbId)
    }

    fun updateEpisodes(series: String, season_number: Int): Int{
        val seasonDetailsResult = seriesRequester.getSeasonDetails(series, season_number)

        seasonDetailsResult?.episodes?.forEach{
            val episode = seriesDbRepository.getEpisodeBySeriesAndNumber(seasonDetailsResult.id.toString(), it.episode_number, it.season_number)
            if(episode.size != 1){
                seriesDbRepository.insert(it.toEpisode(seasonDetailsResult.id.toString(), seasonDetailsResult.name))
            }else if(episode[0].id != null){
                seriesDbRepository.update(episode[0].id?:0, it.name,it.episode_number,it.season_number,it.air_date,it.still_path,it.overview)
            }
        }
        return seasonDetailsResult?.episodes?.size?:0
    }

    fun getSeriesList(): LiveData<List<Series>>{
        return seriesDbRepository.getAllSeries()
    }

    fun clearSearchResult(){
        seriesDbRepository.clearTemporary()
    }



    fun<T> Call<T>.enqueue(callback: CallBackKt<T>.() -> Unit) {
        val callBackKt = CallBackKt<T>()
        callback.invoke(callBackKt)
        this.enqueue(callBackKt)
    }

    class CallBackKt<T>: Callback<T> {

        var onResponse: ((Response<T>) -> Unit)? = null
        var onFailure: ((t: Throwable?) -> Unit)? = null

        override fun onFailure(call: Call<T>, t: Throwable) {
            onFailure?.invoke(t)
        }

        override fun onResponse(call: Call<T>, response: Response<T>) {
            onResponse?.invoke(response)
        }

    }
}
