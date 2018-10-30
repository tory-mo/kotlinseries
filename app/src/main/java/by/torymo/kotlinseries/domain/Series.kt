package by.torymo.kotlinseries.domain

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.Index
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "Series",
        indices = [Index(value = ["imdb_id"],unique = true),
            Index(value = ["mdb_id"], unique = true)])
data class Series(@PrimaryKey(autoGenerate = true) var id: Long?,
                  @ColumnInfo(name = "name") var name: String,
                  @ColumnInfo(name = "original_name") var originalName: String,
                  @ColumnInfo(name = "imdb_id") var imdbId: String,
                  @ColumnInfo(name = "mdb_id") var mdbId: String,
                  @ColumnInfo(name = "poster") var poster: String,
                  @ColumnInfo(name = "watchlist") var watchList: Boolean,
                  @ColumnInfo(name = "genres") var genres: String,
                  @ColumnInfo(name = "first_date") var firstDate: Long,
                  @ColumnInfo(name = "seasons") var seasons: Int,
                  @ColumnInfo(name = "overview") var overview: String,
                  @ColumnInfo(name = "popularity") var popularity: Double,
                  @ColumnInfo(name = "homepage") var homepage: String,
                  @ColumnInfo(name = "networks") var networks: String,
                  @ColumnInfo(name = "followed_season") var followedSeason: Int,
                  @ColumnInfo(name = "temporary") var temporary: Boolean,
                  @ColumnInfo(name = "status") var status: String

){
    constructor():this(null,"","","","","",false,"",0,0,"",0.0,"","",0,true,"")
}