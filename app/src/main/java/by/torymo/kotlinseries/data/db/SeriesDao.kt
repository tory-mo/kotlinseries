package by.torymo.kotlinseries.data.db


import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface SeriesDao {
    @Query("select * from series order by name asc")
    fun getAll(): LiveData<List<Series>>

    @Query("select * from series where watchlist = 1 order by name asc")
    fun getWatchlist(): LiveData<List<Series>>

    @Query("select * from series where imdb_id like :imdbId limit 1")
    fun getSeriesByImdbId(imdbId: String): LiveData<Series>

    @Query("select * from series where imdb_id like :mdbId limit 1")
    fun getSeriesByMdbId(mdbId: String): LiveData<Series>

    @Query("update series set watchlist = :watchlist where imdb_id like :imdbId")
    fun setWatchlist(imdbId: String, watchlist: Boolean)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(series: Series)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(series: List<Series>)

    @Query("delete from series where imdb_id like :imdbId")
    fun deleteByImdbId(imdbId: String)

    @Query("delete from series where mdb_id like :mdbId")
    fun deleteByMdbId(mdbId: String)

}