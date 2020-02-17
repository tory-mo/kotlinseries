package by.torymo.kotlinseries.ui.model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import by.torymo.kotlinseries.SeriesApp
import by.torymo.kotlinseries.data.SeriesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivityViewModel(application: Application): AndroidViewModel(application) {
    private val seriesRepository = getApplication<SeriesApp>().getSeriesRepository()

    fun requestAiringToday(page: Int) = seriesRepository.requestSeries(page, SeriesRepository.Companion.SeriesType.AIRING_TODAY)
    fun requestPopularSeries(page: Int) = seriesRepository.requestSeries(page, SeriesRepository.Companion.SeriesType.POPULAR)

    fun updateEpisodes(){
        GlobalScope.launch {
            val res = seriesRepository.updateEpisodes()
            withContext(Dispatchers.Main){
                if(res) callback?.onUpdated()
            }
        }
    }

    fun setCallback(callback: UpdatedCallback) {
        this.callback = callback
    }

    override fun onCleared() {
        super.onCleared()

        callback = null
    }

    private var callback: UpdatedCallback? = null

    interface UpdatedCallback{
        fun onUpdated()
    }
}