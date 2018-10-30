package by.torymo.kotlinseries.domain

import android.arch.persistence.room.*

@Entity(tableName = "Episodes", foreignKeys = [ForeignKey(entity = Series::class,
        parentColumns = ["imdbid"],
childColumns = ["series"],
onDelete = ForeignKey.CASCADE)],
indices = [Index(value = ["series"])])
data class Episode(@PrimaryKey(autoGenerate = true) var id: Long?,
                   @ColumnInfo(name = "name") var name: String,
                   @ColumnInfo(name = "date") var date: Long,
                   @ColumnInfo(name = "series") var series: String,
                   @ColumnInfo(name = "ep_number") var episodeNumber: Int,
                   @ColumnInfo(name = "s_number") var seasonNumber: Int,
                   @ColumnInfo(name = "seen") var seen: Boolean,
                   @ColumnInfo(name = "overview") var overview: String,
                   @ColumnInfo(name = "poster") var poster: String
        //@Ignore @ColumnInfo(name = "cloud") var cloud: String

){
    constructor():this(null,"",0,"",0,0,false,"","")
}
