package by.torymo.kotlinseries.data.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey


@Entity(tableName = "series",
        indices = [Index(value = ["imdb_id"],unique = true),
            Index(value = ["mdb_id"], unique = true)])
data class Series(@PrimaryKey(autoGenerate = true) var id: Long? = null,
                  @ColumnInfo(name = "name") var name: String = "",
                  @ColumnInfo(name = "original_name") var originalName: String = "",
                  @ColumnInfo(name = "imdb_id") var imdbId: String = "",
                  @ColumnInfo(name = "mdb_id") var mdbId: String = "",
                  @ColumnInfo(name = "poster") var poster: String = "",
                  @ColumnInfo(name = "watchlist") var watchList: Boolean  = true,
                  @ColumnInfo(name = "genres") var genres: String = "",
                  @ColumnInfo(name = "first_date") var firstDate: Long = 0,
                  @ColumnInfo(name = "seasons") var seasons: Int = 0,
                  @ColumnInfo(name = "overview") var overview: String = "",
                  @ColumnInfo(name = "popularity") var popularity: Double = 0.0,
                  @ColumnInfo(name = "homepage") var homepage: String = "",
                  @ColumnInfo(name = "networks") var networks: String = "",
                  @ColumnInfo(name = "followed_season") var followedSeason: Int = 0,
                  @ColumnInfo(name = "temporary") var temporary: Boolean = false,
                  @ColumnInfo(name = "status") var status: String = ""

)