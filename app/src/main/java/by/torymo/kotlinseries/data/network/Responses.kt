package by.torymo.kotlinseries.data.network

import by.torymo.kotlinseries.data.db.Episode
import by.torymo.kotlinseries.data.db.Season
import by.torymo.kotlinseries.data.db.Series

data class SeasonDetailsResponse(
        val id: Int = 0,
        val name: String = "",
        val air_date: Long = 0,
        val overview: String = "",
        val poster_path: String = "",
        val episodes: List<EpisodeResponse>
)

data class EpisodeResponse(
        val id: Int = 0,
        val name: String? = "",
        val air_date: Long = 0,
        val episode_number: Int = 0,
        val overview: String? = "",
        val still_path: String? = ""
){
    fun toEpisode(series: Long, season: Long): Episode{
        return Episode(null, name ?: "", air_date, season, series, episode_number,false, overview ?: "", still_path ?: "")
    }
}

data class SearchResponse(
        val page: Int = 0,
        val total_results: Int = 0,
        val total_pages: Int = 0,
        val results: List<SeriesDetailsResponse> = mutableListOf()
)

data class SeriesDetailsResponse(
        val id: String = "",
        val name: String = "",
        val original_name: String = "",
        val overview: String = "",
        val first_air_date: Long = 0,
        val original_language: String = "",
        val poster_path: String? = "",
        val backdrop_path: String? = "",
        val popularity: Double = 0.0,
        val vote_average: Double = 0.0,
        val vote_count: Int = 0,

        val genres: List<Genre> = mutableListOf(),
        val homepage: String = "",
        val in_production: Boolean = false,
        val last_air_date: Long = 0,
        val networks: List<Network> = mutableListOf(),
        val number_of_seasons: Int = 0,
        val production_companies: List<Network> = mutableListOf(),
        val seasons: List<Seasons> = mutableListOf(),
        val status: String = ""
){
    fun toSeries(): Series{
        return Series(id = id.toLong(),
                name = name,
                originalName = original_name,
                overview = overview,
                firstAirDate = first_air_date,
                lastAirDate = last_air_date,
                originalLanguage = original_language,
                poster = poster_path ?: "",
                backdrop = backdrop_path ?: "",
                popularity = popularity,
                voteAverage = vote_average,
                voteCount = vote_count,
                seasons = seasons.size,
                homepage = homepage,
                temporary = true)
    }
}

data class Genre(val id: Int = 0, val name: String = "")
data class Network(val id: Int = 0, val name: String = "", val logo_path: String = "", val origin_country: String = "")
data class Seasons(val id: Int = 0, val name: String? = "", val air_date: Long = 0, val season_number: Int = 0, val episode_count: Int = 0, val overview: String? = "", val poster_path: String? = ""){
    fun toDbSeason(series: Long): Season{
        return Season(id.toLong(), series, name ?: "", air_date, season_number, episode_count, overview ?: "", poster_path ?: "")
    }
}