package by.torymo.kotlinseries.data


import android.app.Application
import androidx.lifecycle.LiveData
import by.torymo.kotlinseries.data.db.Episode
import by.torymo.kotlinseries.data.db.Series
import by.torymo.kotlinseries.data.network.Requester
import by.torymo.kotlinseries.data.network.SearchResponse
import by.torymo.kotlinseries.data.network.SeriesDetailsResponse
import by.torymo.kotlinseries.ui.DetailActivity
import by.torymo.kotlinseries.ui.fragment.DetailFragment
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

    private fun getEpisodesBetweenDates(dateFrom: Long, dateTo: Long, flag: EpisodeStatus = EpisodeStatus.ALL): LiveData<List<Episode>>{
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

    fun getEpisodesForSeries(series: String): LiveData<List<Episode>> = seriesDbRepository.getEpisodesBySeries(series)

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
                    searchResult?.results?.forEach {
                        val existingSeries = seriesDbRepository.oneSeries(it.id)
                        if(existingSeries == null)
                            seriesDbRepository.insertSeries(it.toSeries())
                        else{
                            seriesDbRepository.updateSeriesMain(
                                    mdbId = it.id,
                                    name = it.name,
                                    originalName = it.original_name,
                                    overview = it.overview,
                                    first_air_date = it.first_air_date,
                                    original_language = it.original_language,
                                    poster_path = if(it.poster_path == null)existingSeries.poster else (Requester.POSTER_PATH + it.poster_path),
                                    backdrop_path = if(it.backdrop_path == null)existingSeries.backdrop else (Requester.POSTER_PATH + it.backdrop_path),
                                    popularity = it.popularity,
                                    vote_average = it.vote_average,
                                    vote_count = it.vote_count
                            )
                        }
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

    fun startFollowingSeries(mdbId: String){
        seriesDbRepository.startFollowingSeries(mdbId)
    }

    fun requestSeriesDetails(mdbId: String, callback: DetailActivity.DetailCallback){
        val call = seriesRequester.getSeriesDetails(mdbId)

        call.enqueue(object: Callback<SeriesDetailsResponse>{
            override fun onResponse(call: Call<SeriesDetailsResponse>, response: Response<SeriesDetailsResponse>) {
                if (response.isSuccessful) {
                    val seriesDetailsResult = response.body()
                    seriesDetailsResult?.let {
                        seriesDbRepository.updateSeriesDetails(mdbId, it.genres.map{it.name}.toString(),it.homepage,it.number_of_seasons, it.status, it.in_production, it.last_air_date, it.networks.map{it.name}.toString())
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

    fun requestSeriesDetailsFr(mdbId: String, callback: DetailFragment.DetailCallback){
        val call = seriesRequester.getSeriesDetails(mdbId)

        call.enqueue(object: Callback<SeriesDetailsResponse>{
            override fun onResponse(call: Call<SeriesDetailsResponse>, response: Response<SeriesDetailsResponse>) {
                if (response.isSuccessful) {
                    val seriesDetailsResult = response.body()
                    seriesDetailsResult?.let {
                        seriesDbRepository.updateSeriesDetails(mdbId, it.genres.map{it.name}.toString(),it.homepage,it.number_of_seasons, it.status, it.in_production, it.last_air_date, it.networks.map{it.name}.toString())
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

    fun getSeriesDetails(mdbId: String): LiveData<Series> = seriesDbRepository.getSeries(mdbId)

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

    fun getSeriesList(): LiveData<List<Series>> = seriesDbRepository.getAllSeries()

    fun clearSearchResult() = seriesDbRepository.clearTemporary()
}
