package by.torymo.kotlinseries.domain

data class MdbSearchResponse(
        val page: Int = 0,
        val total_results: Int = 0,
        val total_pages: Int = 0,
        val results: List<SeriesResponseResult> = mutableListOf()
)