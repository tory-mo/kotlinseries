package by.torymo.kotlinseries.usecases

import by.torymo.kotlinseries.data.SeriesRepository
import by.torymo.kotlinseries.domain.Series

class RequestSeries(private val seriesRepository: SeriesRepository) {

    fun byName(name: String): List<Series> = seriesRepository.requestSeriesByName(name)
    fun byId(id: String): Series = seriesRepository.requestSeriesById(id)

}