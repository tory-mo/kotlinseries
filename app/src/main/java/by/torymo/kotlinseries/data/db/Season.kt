package by.torymo.kotlinseries.data.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey


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