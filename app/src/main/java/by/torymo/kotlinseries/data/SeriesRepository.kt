package by.torymo.kotlinseries.data


import android.app.Application
import androidx.lifecycle.LiveData
import by.torymo.kotlinseries.data.db.Episode
import by.torymo.kotlinseries.data.db.Series
import by.torymo.kotlinseries.data.network.Requester

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

    fun search(query: String, page: Int): LiveData<List<Series>>{
        val searchResult = seriesRequester.search(query, page)
        searchResult?.results?.forEach { seriesDbRepository.insertSeries(it.toSeries()) }

        return seriesDbRepository.getSearchResultSeries()
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
                seriesDbRepository.update(episode[0].id?:0, it.name,it.episode_number,it.season_number,it.air_date?:0,it.still_path,it.overview)
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
}
