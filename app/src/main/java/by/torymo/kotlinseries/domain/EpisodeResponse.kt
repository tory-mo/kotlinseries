package by.torymo.kotlinseries.domain

data class MdbEpisodesResponse(val items: List<EpisodeResponse>)

data class EpisodeResponse(
        val name: String = "",
        val air_date: String = "",
        val episode_number: Int = 0,
        val overview: String = "",
        val season_number: Int = 0,
        val still_path: String = ""
)