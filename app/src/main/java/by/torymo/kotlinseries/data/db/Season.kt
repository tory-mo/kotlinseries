package by.torymo.kotlinseries.data.db

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.arch.persistence.room.Index
import android.arch.persistence.room.ColumnInfo

@Entity(tableName = "seasons",
        indices = [Index(value = ["series"])])
data class Season(@PrimaryKey(autoGenerate = true) var id: Long? = null,
                   @ColumnInfo(name = "name") var name: String = "",
                   @ColumnInfo(name = "date") var date: Long = 0,
                   @ColumnInfo(name = "series") var series: String = "",
                   @ColumnInfo(name = "number") var number: Int = 0,
                   @ColumnInfo(name = "overview") var overview: String = "",
                   @ColumnInfo(name = "poster") var poster: String = ""
        //@Ignore @ColumnInfo(name = "cloud") var cloud: String

)