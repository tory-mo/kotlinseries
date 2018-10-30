package by.torymo.kotlinseries.data

import by.torymo.kotlinseries.domain.Series

class SeriesRepository(private val seriesPersistenceSource: SeriesPersistenceSource,
                       private val natworkSeriesSource: NetworkSeriesSource){
    fun getSavedSeries(): List<Series> = seriesPersistenceSource.getPersistedSeries()
    fun getSavedSeries(id: String): Series = seriesPersistenceSource.getPersistedSeries(id)

    fun requestSeriesById(id: String): Series {
        val newLocation = natworkSeriesSource.getSeriesById(id)
        seriesPersistenceSource.saveSeries(newLocation)
        return getSavedSeries(id)
    }

    fun requestSeriesByName(name: String): List<Series> {
        val newLocation = natworkSeriesSource.getSeriesByName(name)
        seriesPersistenceSource.saveSeries(newLocation)
        return getSavedSeries()
    }

    fun requestSeriesDetails(id: String): Series {
        val newLocation = natworkSeriesSource.getSeriesDetail(id)
        seriesPersistenceSource.saveSeries(newLocation)
        return getSavedSeries(id)
    }
}


interface SeriesPersistenceSource {
    fun getPersistedSeries(): List<Series>
    fun getPersistedSeries(id: String): Series
    fun saveSeries(series: Series)
    fun saveSeries(series: List<Series>)

}

interface NetworkSeriesSource {
    fun getSeriesDetail(id: String): Series
    fun getSeriesById(id: String): Series
    fun getSeriesByName(name: String): List<Series>

}