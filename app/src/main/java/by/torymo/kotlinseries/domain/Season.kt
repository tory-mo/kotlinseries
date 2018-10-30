package by.torymo.kotlinseries.domain

import android.arch.persistence.room.*

@Entity(tableName = "Seasons",
        indices = [Index(value = ["series"])])
data class Season(@PrimaryKey(autoGenerate = true) var id: Long?,
                   @ColumnInfo(name = "name") var name: String,
                   @ColumnInfo(name = "date") var date: Long,
                   @ColumnInfo(name = "series") var series: String,
                   @ColumnInfo(name = "number") var number: Int,
                   @ColumnInfo(name = "overview") var overview: String,
                   @ColumnInfo(name = "poster") var poster: String
        //@Ignore @ColumnInfo(name = "cloud") var cloud: String

){
    constructor():this(null,"",0,"",0,"","")
}