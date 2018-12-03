package by.torymo.kotlinseries.data.db


import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface SeriesDao {
    @Query("select * from series where 'temporary' = 0 order by name asc")
    fun getAll(): LiveData<List<Series>>

    @Query("select * from series where 'temporary' = 1 order by name asc")
    fun getTemporary(): LiveData<List<Series>>

    @Query("UPDATE series set 'temporary' = 1 where mdb_id = :mdbId")
    fun changeToPersistent(mdbId: String)

    @Query("update series set genres = :genres, homepage = :homepage, seasons = :number_of_seasons, status = :status where mdb_id = :mdbId")
    fun updateDetails(mdbId: String, genres: String, homepage: String, number_of_seasons: Int, status: String)

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