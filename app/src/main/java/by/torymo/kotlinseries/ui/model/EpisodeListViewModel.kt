package by.torymo.kotlinseries.ui.model

import android.app.Application


import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import by.torymo.kotlinseries.SeriesApp
import by.torymo.kotlinseries.data.db.ExtendedEpisode

class EpisodeListViewModel(application: Application): AndroidViewModel(application){
    private val seriesRepository = getApplication<SeriesApp>().getSeriesRepository()
    private val seasonId = MutableLiveData<Long>()

    fun getEpisodesByMdbId(season: Long): LiveData<List<ExtendedEpisode>> {
        seasonId.value = season

        return  Transformations.switchMap<Long, List<ExtendedEpisode>>(seasonId) { id ->
            seriesRepository.getEpisodesForSeason(id)
        }
    }

    fun changeEpisodeSeen(id: Long, seen: Boolean = false){
        seriesRepository.changeEpisodeSeen(id, seen)
    }
}