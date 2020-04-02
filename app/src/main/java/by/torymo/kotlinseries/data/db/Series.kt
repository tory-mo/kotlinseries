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
                  @ColumnInfo(name = "poster_path") var poster: String = "",
                  @ColumnInfo(name = "original_language") var originalLanguage: String = "",
                  @ColumnInfo(name = "backdrop_path") var backdrop: String = "",
                  @ColumnInfo(name = "popularity") var popularity: Double = 0.0,
                  @ColumnInfo(name = "vote_average") var voteAverage: Double = 0.0,
                  @ColumnInfo(name = "vote_count") var voteCount: Int = 0,

                  @ColumnInfo(name = "genres") var genres: String = "",
                  @ColumnInfo(name = "homepage") var homepage: String = "",
                  @ColumnInfo(name = "in_production") var inProduction: Boolean  = true,
                  @ColumnInfo(name = "last_air_date") var lastAirDate: Long = 0,
                  @ColumnInfo(name = "networks") var networks: String = "",
                  @ColumnInfo(name = "seasons") var seasons: Int = 0,
                  @ColumnInfo(name = "status") var status: String = "",

                  @ColumnInfo(name = "type") var type: Int  = 1, // watchlist - 0, search result - 1, airing today - 2
                  @ColumnInfo(name = "temporary_row") var temporary: Boolean = false,


                  @ColumnInfo(name = "type_watchlist") var watchlist: Boolean = false,
                  @ColumnInfo(name = "type_search") var search: Boolean = false,
                  @ColumnInfo(name = "type_airing") var airing: Boolean = false,
                  @ColumnInfo(name = "type_popular") var popular: Boolean = false
): Serializable