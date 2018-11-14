package by.torymo.kotlinseries.ui

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MediatorLiveData
import by.torymo.kotlinseries.Requester
import by.torymo.kotlinseries.SeriesApp
import by.torymo.kotlinseries.domain.Episode

class EpisodeCalendarViewModel(application: Application): AndroidViewModel(application) {
    private val seriesDbRepository = getApplication<SeriesApp>().getSeriesRepository()
    val episodeDates = MediatorLiveData<List<Long>>()
    val episodeList = MediatorLiveData<List<Episode>>()

    fun getEpisodeDatesBetweenDates(date1: Long, date2: Long, flag: Int = 1){
        if(flag == 0){// not seen
            episodeDates.addSource(seriesDbRepository.getEpisodeDatesBetweenDates(date1, date2)){
                dates->episodeDates.postValue(dates)
            }
        }else{//all
            episodeDates.addSource(seriesDbRepository.getEpisodeDatesBetweenDates(date1, date2)){
                dates->episodeDates.postValue(dates)
            }
        }
    }

    fun getEpisodeList():LiveData<List<Episode>>{
        return episodeList
    }

    fun getEpisodesForDate(date: Long, flag: Int = 1){
        if(flag == 0){// not seen
            episodeList.addSource(seriesDbRepository.getNotSeenEpisodesBetweenDates(date, date)){
                episodes->episodeList.postValue(episodes)
            }
        }else{//all
            episodeList.addSource(seriesDbRepository.getEpisodesBetweenDates(date, date)){
                episodes->episodeList.postValue(episodes)
            }
        }
    }

    fun updateEpisodes(){
        val
    }
}