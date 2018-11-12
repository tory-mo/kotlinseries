package by.torymo.kotlinseries.domain

data class SeriesResponseResult(
        val name: String = "",
        val original_name: String = "",
        val id: String = "",
        val poster_path: String = "",
        val external_ids: String = "",
        val in_production: Boolean = false,
        val number_of_seasons: Int = 0,
        val overview: String = "",
        val status: String = "",
        val homepage: String = "",
        val popularity: Double = 0.0
)