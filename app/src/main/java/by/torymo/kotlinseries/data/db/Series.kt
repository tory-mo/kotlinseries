package by.torymo.kotlinseries.data.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable


@Entity(tableName = "series")
data class Series(@PrimaryKey @ColumnInfo(name = "id") var id: Long = 0,
                  @ColumnInfo(name = "name") var name: String = "",
                  @ColumnInfo(name = "original_name") var originalName: String = "",
                  @ColumnInfo(name = "overview") var overview: String = "",
                  @ColumnInfo(name = "first_air_date") var firstAirDate: Long = 0,
                  @ColumnInfo(name = "last_air_date") var lastAirDate: Long = 0,
                  @ColumnInfo(name = "next_episode") var nextEpisodeDate: Long = 0,
                  @ColumnInfo(name = "episode_runtime") var episodeRunTime: Int = 0,
                  @ColumnInfo(name = "poster_path") var poster: String = "",
                  @ColumnInfo(name = "original_language") var originalLanguage: String = "",
                  @ColumnInfo(name = "backdrop_path") var backdrop: String = "",
                  @ColumnInfo(name = "popularity") var popularity: Double = 0.0,
                  @ColumnInfo(name = "vote_average") var voteAverage: Double = 0.0,
                  @ColumnInfo(name = "vote_count") var voteCount: Int = 0,

                  @ColumnInfo(name = "genres") var genres: String = "",
                  @ColumnInfo(name = "homepage") var homepage: String = "",
                  @ColumnInfo(name = "in_production") var inProduction: Boolean  = true,

                  @ColumnInfo(name = "networks") var networks: String = "",
                  @ColumnInfo(name = "seasons") var seasons: Int = 0,
                  @ColumnInfo(name = "status") var status: String = "",
                  @ColumnInfo(name = "certification") var certification: String = ""
): Serializable