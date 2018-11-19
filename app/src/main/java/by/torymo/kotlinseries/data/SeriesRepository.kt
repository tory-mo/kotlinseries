package by.torymo.kotlinseries.data


import android.app.Application
import androidx.lifecycle.LiveData
import by.torymo.kotlinseries.data.db.Episode
import by.torymo.kotlinseries.data.db.Series
import by.torymo.kotlinseries.data.network.Requester

class SeriesRepository(application: Application){

    private val seriesDbRepository = SeriesDbRepository(application)
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

    fun updateEpisodes(series: String, season_number: Long){
        Requester().updateEpisodes(series, season_number)
    }

    fun getSeriesList(): LiveData<List<Series>>{
        return seriesDbRepository.getAllSeries()
    }
}
