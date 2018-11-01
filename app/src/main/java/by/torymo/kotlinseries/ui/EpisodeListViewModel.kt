package by.torymo.kotlinseries.ui

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import by.torymo.kotlinseries.SeriesApp
import by.torymo.kotlinseries.domain.Episode

class EpisodeListViewModel(application: Application): AndroidViewModel(application){
    private val seriesDbRepository = getApplication<SeriesApp>().getSeriesRepository()
    private val seriesId = MutableLiveData<String>()

    fun getEpisodesByMdbId(mdbId: String): LiveData<List<Episode>>{
        seriesId.value = mdbId

        return  Transformations.switchMap<String, List<Episode>>(seriesId) { id ->
            seriesDbRepository.getEpisodesBySeries(id)
        }
    }
}