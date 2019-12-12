package by.torymo.kotlinseries.data.db

import androidx.room.*


@Entity(tableName = "seasons",
        foreignKeys = [ForeignKey(entity = Series::class,
                parentColumns = ["id"],
                childColumns = ["series_id"],
                onDelete = ForeignKey.CASCADE)],
        indices = [Index(value = ["series_id"])])
data class Season(@PrimaryKey var id: Long = 0,
                  @ColumnInfo(name = "series_id") var series: Long = 0,
                  @ColumnInfo(name = "name") var name: String = "",
                  @ColumnInfo(name = "date") var date: Long = 0,
                  @ColumnInfo(name = "number") var number: Int = 0,
                  @ColumnInfo(name = "episodes_count") var episodes: Int = 0,
                  @ColumnInfo(name = "overview") var overview: String = "",
                  @ColumnInfo(name = "poster") var poster: String = "",
                  @ColumnInfo(name = "following") var following: Boolean = false
        //@Ignore @ColumnInfo(name = "cloud") var cloud: String
)