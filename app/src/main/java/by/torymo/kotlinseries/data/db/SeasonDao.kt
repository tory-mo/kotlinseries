package by.torymo.kotlinseries.data.db


import androidx.lifecycle.LiveData
import androidx.room.*


@Dao
interface SeasonDao {

    @Query("select * from seasons where id = :season")
    fun getById(season: Long): Season?

    @Query("select * from seasons where series_id = :series order by number desc")
    fun get(series: Long): LiveData<List<Season>>

    @Query("select * from seasons where series_id = :series and following = 1 order by number desc")
    fun getFollowing(series: Long): List<Season>

    @Query("select * from seasons where series_id = :series order by number desc limit 1")
    fun getLast(series: Long): Season

    @Query("update seasons set following = :following where id = :season")
    fun updateFollowing(season: Long, following: Boolean)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(seasons: List<Season>)

    @Update
    fun update(seasons: List<Season>)

}