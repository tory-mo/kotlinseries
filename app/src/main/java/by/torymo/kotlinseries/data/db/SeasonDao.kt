package by.torymo.kotlinseries.data.db

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query

@Dao
interface SeasonDao {
    @Query("select * from seasons order by name asc")
    fun getAll(): LiveData<List<Season>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(season: Season)

    @Query("delete from seasons where series like :imdbId")
    fun deleteByImdbId(imdbId: String)
}