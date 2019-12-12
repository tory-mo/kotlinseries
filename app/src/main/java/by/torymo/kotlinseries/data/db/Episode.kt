package by.torymo.kotlinseries.data.db

import androidx.room.*


@Entity(tableName = "episodes",
        foreignKeys = [
            ForeignKey(entity = Series::class,
                        parentColumns = ["id"],
                        childColumns = ["seriesId"],
                        onDelete = ForeignKey.CASCADE),
            ForeignKey(entity = Season::class,
                        parentColumns = ["id"],
                        childColumns = ["seasonId"],
                        onDelete = ForeignKey.CASCADE)],
        indices = [Index(value = ["seriesId"]), Index(value = ["seasonId"])])
data class Episode(
        @PrimaryKey(autoGenerate = true) var id: Long? = null,
        @ColumnInfo(name = "name") var name: String = "",
        @ColumnInfo(name = "date") var date: Long = 0,
        @ColumnInfo(name = "seasonId") var season: Long = 0,
        @ColumnInfo(name = "seriesId") var series: Long = 0,
        @ColumnInfo(name = "ep_number") var episodeNumber: Int = 0,
        @ColumnInfo(name = "seen") var seen: Boolean = false,
        @ColumnInfo(name = "overview") var overview: String = "",
        @ColumnInfo(name = "poster") var poster: String = ""
)
