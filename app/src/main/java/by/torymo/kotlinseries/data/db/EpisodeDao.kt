package by.torymo.kotlinseries.data.db


import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface EpisodeDao {
    @Query("SELECT * FROM episodes order by date asc")
    fun getAll(): LiveData<List<Episode>>

    @Query("SELECT distinct date FROM episodes order by date asc")
    fun getAllDates(): LiveData<List<Long>>

    @Query("SELECT * FROM episodes WHERE series like :series order by date asc")
    fun getEpisodesBySeries(series: String): LiveData<List<Episode>>

    @Query("SELECT * FROM episodes WHERE series like :series and ep_number = :epNumber and s_number = :sNumber order by date asc")
    fun getEpisodesBySeriesAndNumber(series: String, epNumber: Int, sNumber: Int): List<Episode>

    //@Query("SELECT * FROM episodes WHERE date between :date1 and :date2 order by date asc")
    @Query("select episodes.*, series.name as series_name from episodes inner join series on series.mdb_id=episodes.series where episodes.date between :date1 and :date2 order by episodes.date asc")
    fun getEpisodesBetweenDates(date1: Long, date2: Long): LiveData<List<Episode>>

    //@Query("SELECT * FROM episodes WHERE (seen = 0) and (date between :date1 and :date2) order by date asc")
    @Query("select episodes.*, series.name as series_name from episodes inner join series on series.mdb_id=episodes.series where (episodes.seen = 0) and (episodes.date between :date1 and :date2) order by episodes.date asc")
    fun getNotSeenEpisodesBetweenDates(date1: Long, date2: Long): LiveData<List<Episode>>

    @Query("SELECT date FROM episodes WHERE date between :date1 and :date2 order by date asc")
    fun getEpisodeDatesBetweenDates(date1: Long, date2: Long): LiveData<List<Long>>

    @Query("SELECT date FROM episodes WHERE (seen = 0) and (date between :date1 and :date2) order by date asc")
    fun getNotSeenEpisodeDatesBetweenDates(date1: Long, date2: Long): LiveData<List<Long>>

//    @Query("SELECT * FROM episodes WHERE date = :date")
//    fun getEpisodesForDate(date: Long): List<Episode>
//
//    @Query("SELECT * FROM episodes WHERE date = :date and seen = 0")
//    fun getNotSeenEpisodesForDate(date: Long): List<Episode>

    @Query("SELECT * FROM episodes WHERE date = :date and series like :series")
    fun getEpisodesForSeriesAndDate(series: String, date: Long): LiveData<List<Episode>>

    @Query("UPDATE episodes set seen = :seen where id = :id")
    fun setSeen(id: Long, seen: Boolean)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(episode: Episode)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(episodes: List<Episode>)

    @Query("Update episodes set name = :name, ep_number = :epNumber, s_number = :sNumber, date = :date, poster = :mPoster, overview = :mOverview where id = :id")
    fun update(id: Long, name: String, epNumber: Int, sNumber: Int, date: Long, mPoster: String, mOverview: String)

    @Query("Delete from episodes where series like :series")
    fun delete(series: String)

}