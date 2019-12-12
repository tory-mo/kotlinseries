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
    private val seriesId = MutableLiveData<Long>()

    fun getEpisodesByMdbId(mdbId: Long): LiveData<List<ExtendedEpisode>> {
        seriesId.value = mdbId

        return  Transformations.switchMap<Long, List<ExtendedEpisode>>(seriesId) { id ->
            seriesRepository.getEpisodesForSeries(id)
        }
    }

    fun changeEpisodeSeen(id: Long, seen: Boolean = false){
        seriesRepository.changeEpisodeSeen(id, seen)
    }
}