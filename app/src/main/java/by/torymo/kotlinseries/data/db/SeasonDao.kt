package by.torymo.kotlinseries.data.db


import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query


@Dao
interface SeasonDao {
    @Query("select * from seasons order by series asc")
    fun getAll(): LiveData<List<Season>>

    @Query("select * from seasons where series = :series order by number asc")
    fun get(series: String): LiveData<List<Season>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(season: Season)

    @Query("delete from seasons where series like :series")
    fun delete(series: String)
}