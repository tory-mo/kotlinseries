package by.torymo.kotlinseries.domain

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.arch.persistence.room.ForeignKey
import android.arch.persistence.room.Index
import android.arch.persistence.room.ColumnInfo

@Entity(tableName = "episodes", foreignKeys = [ForeignKey(entity = Series::class,
        parentColumns = ["imdbid"],
childColumns = ["series"],
onDelete = ForeignKey.CASCADE)],
indices = [Index(value = ["series"])])
data class Episode(
        @PrimaryKey(autoGenerate = true) var id: Long? = null,
        @ColumnInfo(name = "name") var name: String = "",
        @ColumnInfo(name = "date") var date: Long = 0,
        @ColumnInfo(name = "series") var series: String = "",
        @ColumnInfo(name = "ep_number") var episodeNumber: Int = 0,
        @ColumnInfo(name = "s_number") var seasonNumber: Int = 0,
        @ColumnInfo(name = "seen") var seen: Boolean = false,
        @ColumnInfo(name = "overview") var overview: String = "",
        @ColumnInfo(name = "poster") var poster: String = ""
        //@Ignore @ColumnInfo(name = "cloud") var cloud: String

)
