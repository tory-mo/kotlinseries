package by.torymo.kotlinseries.ui.model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import by.torymo.kotlinseries.SeriesApp
import by.torymo.kotlinseries.data.db.ExtendedEpisode

class CalendarViewModel(application: Application): AndroidViewModel(application) {

    interface EpisodesCallback{
        fun onEpisodesForDateComplete(episodes: List<ExtendedEpisode>)
        fun onEpisodesBetweenDatesComplete(dates: List<Long>)
    }

    private var callback: EpisodesCallback? = null

    private val seriesRepository = getApplication<SeriesApp>().getSeriesRepository()

    fun setEpisodeListCallback(callback: EpisodesCallback) {
        this.callback = callback
    }

    fun getEpisodeDatesBetweenDates(date1: Long, date2: Long){

        callback?.onEpisodesBetweenDatesComplete(seriesRepository.getEpisodeDatesBetweenDates(date1, date2, seriesRepository.getSeenStatus()))
    }

    fun getEpisodesForDate(date: Long){
        callback?.onEpisodesForDateComplete(seriesRepository.getEpisodesForDay(date, seriesRepository.getSeenStatus()))
    }

    fun changeEpisodeSeen(id: Long, seen: Boolean = false){
        seriesRepository.changeEpisodeSeen(id, seen)
    }

    fun getSeenParam(): Boolean{
        return seriesRepository.getSeenParam()
    }

    fun changeSeenParam(){
        seriesRepository.changeSeenParam()
    }

    override fun onCleared() {
        super.onCleared()
        callback = null
    }
}