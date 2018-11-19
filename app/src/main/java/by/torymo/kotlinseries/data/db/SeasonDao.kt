package by.torymo.kotlinseries.data.db


import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query


@Dao
interface SeasonDao {
    @Query("select * from seasons order by name asc")
    fun getAll(): LiveData<List<Season>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(season: Season)

    @Query("delete from seasons where series like :imdbId")
    fun deleteByImdbId(imdbId: String)
}