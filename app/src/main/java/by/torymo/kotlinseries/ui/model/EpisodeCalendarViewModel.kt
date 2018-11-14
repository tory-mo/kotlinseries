package by.torymo.kotlinseries.ui.model

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MediatorLiveData
import by.torymo.kotlinseries.SeriesApp
import by.torymo.kotlinseries.data.SeriesRepository
import by.torymo.kotlinseries.data.SeriesRepository.Companion.EpisodeStatus
import by.torymo.kotlinseries.data.db.Episode
import retrofit2.Callback

class EpisodeCalendarViewModel(application: Application): AndroidViewModel(application) {
    private val seriesRepository = getApplication<SeriesApp>().getSeriesRepository()
    val episodeDates = MediatorLiveData<List<Long>>()
    val episodeList = MediatorLiveData<List<Episode>>()

    fun getEpisodeDatesBetweenDates(date1: Long, date2: Long, flag: EpisodeStatus = EpisodeStatus.ALL){
        episodeDates.addSource(seriesRepository.getEpisodeDatesBetweenDates(date1, date2, flag)){
            dates->episodeDates.postValue(dates)
        }
    }

    fun getEpisodeList():LiveData<List<Episode>>{
        return episodeList
    }

    fun getEpisodesForDate(date: Long, flag: SeriesRepository.Companion.EpisodeStatus = EpisodeStatus.ALL){
        episodeList.addSource(seriesRepository.getEpisodesForDay(date, flag)){
            episodes->episodeList.postValue(episodes)
        }
    }

    fun changeEpisodeSeen(id: Long?, seen: EpisodeStatus = EpisodeStatus.NOT_SEEN){
        seriesRepository.changeEpisodeSeen(id, seen)
    }

    fun updateEpisodes(series: String, callback: Callback<List<Episode>>){
        seriesRepository.updateEpisodes(series, callback)
    }
}