package by.torymo.kotlinseries.ui.model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import by.torymo.kotlinseries.SeriesApp
import by.torymo.kotlinseries.data.SeriesRepository.Companion.EpisodeStatus
import by.torymo.kotlinseries.data.db.ExtendedEpisode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CalendarViewModel(application: Application): AndroidViewModel(application) {

    interface EpisodesCallback{

        fun onEpisodesForDateComplete(episodes: List<ExtendedEpisode>)
        fun onEpisodesBetweenDatesComplete(dates: List<Long>)
        fun onEpisodesUpdated()
    }

    private var callback: EpisodesCallback? = null

    private val seriesRepository = getApplication<SeriesApp>().getSeriesRepository()

    fun requestAiringToday() = seriesRepository.requestAiringTodaySeries()

    fun setEpisodeListCallback(callback: EpisodesCallback) {
        this.callback = callback
    }

    fun getEpisodeDatesBetweenDates(date1: Long, date2: Long, flag: EpisodeStatus = EpisodeStatus.ALL){
        callback?.onEpisodesBetweenDatesComplete(seriesRepository.getEpisodeDatesBetweenDates(date1, date2, flag))
    }

    fun getEpisodesForDate(date: Long, flag: EpisodeStatus = EpisodeStatus.ALL){
        callback?.onEpisodesForDateComplete(seriesRepository.getEpisodesForDay(date, flag))
    }

    fun changeEpisodeSeen(id: Long, seen: Boolean = false){
        seriesRepository.changeEpisodeSeen(id, seen)
    }

    fun updateEpisodes(){
        GlobalScope.launch {
            val res = seriesRepository.updateEpisodes()
            withContext(Dispatchers.Main){
                if(res) callback?.onEpisodesUpdated()
            }
        }
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