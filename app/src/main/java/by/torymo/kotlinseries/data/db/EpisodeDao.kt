package by.torymo.kotlinseries.data.db

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import by.torymo.kotlinseries.domain.Episode
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query

@Dao
interface EpisodeDao {
    @Query("SELECT * FROM episodes order by date asc")
    fun getAll(): LiveData<List<Episode>>

    @Query("SELECT * FROM episodes WHERE series like :series order by date asc")
    fun getEpisodesBySeries(series: String): LiveData<List<Episode>>

    @Query("SELECT * FROM episodes WHERE series like :series and ep_number = :epNumber and s_number = :sNumber order by date asc")
    fun getEpisodesBySeriesAndNumber(series: String, epNumber: Int, sNumber: Int): LiveData<List<Episode>>

    @Query("SELECT * FROM episodes WHERE date between :date1 and :date2 order by date asc")
    fun getEpisodesBetweenDates(date1: Long, date2: Long): LiveData<List<Episode>>

    @Query("SELECT * FROM episodes WHERE (seen = 0) and (date between :date1 and :date2) order by date asc")
    fun getNotSeenEpisodesBetweenDates(date1: Long, date2: Long): LiveData<List<Episode>>

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
    fun update(id: Long, name: String, epNumber: Long, sNumber: Long, date: Long, mPoster: String, mOverview: String)

    @Query("Delete from episodes where series like :series")
    fun delete(series: String)
}