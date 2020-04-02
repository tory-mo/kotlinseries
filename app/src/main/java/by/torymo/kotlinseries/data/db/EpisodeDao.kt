package by.torymo.kotlinseries.data.db

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface EpisodeDao {

    @Query("SELECT episodes.*, series.name as seriesName, seasons.number as seasonNumber from episodes inner join series on series.id=episodes.seriesId inner join seasons on seasons.id = episodes.seasonId WHERE seriesId = :series order by date asc")
    fun getEpisodesBySeries(series: Long): LiveData<List<ExtendedEpisode>>

    @Query("SELECT episodes.*, series.name as seriesName, seasons.number as seasonNumber from episodes inner join series on series.id=episodes.seriesId inner join seasons on seasons.id = episodes.seasonId WHERE seasonId = :season order by date asc")
    fun getEpisodesBySeason(season: Long): LiveData<List<ExtendedEpisode>>

    @Query("SELECT * FROM episodes WHERE seasonId = :season and ep_number = :epNumber limit 1")
    fun getEpisodesBySeasonAndNumber(season: Long, epNumber: Int): Episode?

    @Query("SELECT distinct date FROM episodes WHERE date between :date1 and :date2 order by date asc")
    fun getEpisodeDatesBetweenDates(date1: Long, date2: Long): List<Long>

    @Query("SELECT distinct date FROM episodes WHERE (seen = 0) and (date between :date1 and :date2) order by date asc")
    fun getNotSeenEpisodeDatesBetweenDates(date1: Long, date2: Long): List<Long>

    @Query("SELECT episodes.*, series.name as seriesName, seasons.number as seasonNumber from episodes inner join series on series.id=episodes.seriesId inner join seasons on seasons.id = episodes.seasonId WHERE episodes.date = :date order by seriesName, ep_number asc")
    fun getEpisodesForDate(date: Long): List<ExtendedEpisode>

    @Query("SELECT episodes.*, series.name as seriesName, seasons.number as seasonNumber from episodes inner join series on series.id=episodes.seriesId inner join seasons on seasons.id = episodes.seasonId WHERE (seen = 0) and (episodes.date = :date) order by seriesName, ep_number asc")
    fun getNotSeenEpisodesForDate(date: Long): List<ExtendedEpisode>

    @Query("UPDATE episodes set seen = :seen where id = :id")
    fun setSeen(id: Long, seen: Boolean)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(episode: Episode)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(episodes: List<Episode>)

    @Update
    fun update(episode: Episode)

    @Query("Delete from episodes where seriesId = :series")
    fun deleteBySeries(series: Long)

    @Query("Delete from episodes where seasonId = :season")
    fun deleteBySeason(season: Long)

}