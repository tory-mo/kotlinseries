package by.torymo.kotlinseries.data.db

data class ExtendedEpisode(var id: Long? = null,
                           var name: String = "",
                           var date: Long = 0,
                           var seasonId: Long = 0,
                           var seasonNumber: Int = 0,
                           var seriesId: Long = 0,
                           var seriesName: String = "",
                           var ep_number: Int = 0,
                           var seen: Boolean = false,
                           var overview: String = "",
                           var poster: String = "")