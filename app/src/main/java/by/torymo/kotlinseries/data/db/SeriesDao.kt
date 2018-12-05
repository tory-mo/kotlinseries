package by.torymo.kotlinseries.data.db


import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface SeriesDao {
    @Query("select * from series where temporary_row = 0 order by name asc")
    fun getAll(): LiveData<List<Series>>

    @Query("select * from series where temporary_row = 1 order by popularity desc")
    fun getTemporary(): LiveData<List<Series>>

    @Query("UPDATE series set temporary_row = 1, watchlist = 1 where mdb_id = :mdbId")
    fun changeToPersistent(mdbId: String)

    @Query("update series set genres = :genres, homepage = :homepage, seasons = :number_of_seasons, status = :status, in_production = :in_production, last_air_date = :lastAirDate, networks = :networks where mdb_id = :mdbId")
    fun updateDetails(mdbId: String, genres: String, homepage: String, number_of_seasons: Int, status: String, in_production:Boolean, lastAirDate:Long, networks: String)

    @Query("select * from series where watchlist = 1 order by name asc")
    fun getWatchlist(): LiveData<List<Series>>

    @Query("select * from series where mdb_id like :mdbId limit 1")
    fun getSeries(mdbId: String): LiveData<Series>

    @Query("update series set watchlist = :watchlist where mdb_id like :mdbId")
    fun setWatchlist(mdbId: String, watchlist: Boolean)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(series: Series)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(series: List<Series>)

    @Query("delete from series where mdb_id like :mdbId")
    fun delete(mdbId: String)

    @Query("delete from series where temporary_row = 1")
    fun deleteTemporary()

}