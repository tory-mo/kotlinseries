package by.torymo.kotlinseries.data.network

import by.torymo.kotlinseries.data.db.Episode
import by.torymo.kotlinseries.data.db.Season
import by.torymo.kotlinseries.data.db.Series
import java.util.*

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

        val genres: List<Genre>?,
        val homepage: String?,
        val in_production: Boolean = false,
        val last_air_date: Long = 0,
        val episode_run_time: List<Int>?,
        val next_episode_to_air: EpisodeResponse?,
        val networks: List<Network>?,
        val number_of_seasons: Int = 0,
        val production_companies: List<Network>?,
        val seasons: List<Seasons>?,
        val status: String = "",
        val content_ratings: AppendResponse<List<ContentRating>>?,
        val aggregate_credits: AggregateCredits?
){
    companion object{
        fun toSeries(item: SeriesDetailsResponse): Series{
            return Series(id = item.id.toLong(),
                    name = item.name,
                    originalName = item.original_name,
                    overview = item.overview,
                    firstAirDate = item.first_air_date,
                    lastAirDate = item.last_air_date,
                    nextEpisodeDate = item.next_episode_to_air?.air_date ?: 0,
                    originalLanguage = item.original_language,
                    poster = item.poster_path ?: "",
                    backdrop = item.backdrop_path ?: "",
                    popularity = item.popularity,
                    voteAverage = item.vote_average,
                    voteCount = item.vote_count,
                    seasons = item.number_of_seasons,
                    homepage = item.homepage ?: "",
                    episodeRunTime = if(item.episode_run_time != null && item.episode_run_time.isNotEmpty()) item.episode_run_time[0] else 0,
                    genres = item.genres?.joinToString { it.name } ?: "",
                    networks = item.networks?.joinToString { it.name } ?: "",
                    inProduction = item.in_production,
                    certification = if(item.content_ratings != null) contentRating(item.content_ratings.results) else ""
            )
        }

        fun toSeries(items: List<SeriesDetailsResponse>): List<Series>{
            val series = mutableListOf<Series>()
            for (item in items){
                series.add(toSeries(item))
            }
            return series
        }

        private fun contentRating(ratings: List<ContentRating>): String{
            val country = Locale.getDefault().country
            return ratings.find { it.iso_3166_1 == country }?.rating ?: ""
        }
    }
}
data class AggregateCredits(val cast: List<Cast>)
data class Cast(val id: Int, val name: String, val profile_path: String, val total_episodes_count: Int)
data class AppendResponse<T>(val results: T)
data class ContentRating(val iso_3166_1: String, val rating: String)
data class Genre(val id: Int = 0, val name: String = "")
data class Network(val id: Int = 0, val name: String = "", val logo_path: String = "", val origin_country: String = "")
data class Seasons(val id: Int = 0, val name: String? = "", val air_date: Long = 0, val season_number: Int = 0, val episode_count: Int = 0, val overview: String? = "", val poster_path: String? = ""){
    fun toDbSeason(series: Long): Season{
        return Season(id.toLong(), series, name ?: "", air_date, season_number, episode_count, overview ?: "", poster_path ?: "")
    }

    companion object{
        fun toSeason(series: Long, item: Seasons): Season{
            return Season(item.id.toLong(), series, item.name ?: "", item.air_date, item.season_number, item.episode_count, item.overview ?: "", item.poster_path ?: "")
        }

        fun toSeason(series: Long, items: List<Seasons>?): List<Season>{
            val seasons = mutableListOf<Season>()

            items?.let {
                for (item in items){
                    seasons.add(toSeason(series, item))
                }
            }

            return seasons
        }
    }
}