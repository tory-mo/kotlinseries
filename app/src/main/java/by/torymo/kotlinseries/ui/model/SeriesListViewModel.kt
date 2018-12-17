package by.torymo.kotlinseries.ui.model


import android.app.Application
import androidx.lifecycle.*
import by.torymo.kotlinseries.SeriesApp
import by.torymo.kotlinseries.data.db.Series
import by.torymo.kotlinseries.ui.fragment.SeriesFragment

class SeriesListViewModel(application: Application): AndroidViewModel(application) {
    private val seriesRepository = getApplication<SeriesApp>().getSeriesRepository()
    private val queryString = MutableLiveData<String>()
    private val pageNum = MutableLiveData<Int>()

    val seriesList: LiveData<List<Series>> = Transformations.switchMap(queryString){
        query -> seriesRepository.getSeriesByName(query)
    }

    fun searchSeries(query: String, page: Int,  callback: SeriesFragment.SearchCallback){
        queryString.value = query
        pageNum.value = page

        seriesRepository.search(query, page, callback)
    }

    fun clearSearch(){
        queryString.value = ""
        seriesRepository.clearSearchResult()
    }
}