package by.torymo.kotlinseries.data.db


import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface SeriesDao {
    @Query("select * from series where temporary_row = 0 order by name asc")
    fun getAll(): LiveData<List<Series>>

    @Query("select * from series where temporary_row = 1 or name like '%' || :name  || '%' order by popularity desc")
    fun getByName(name: String): LiveData<List<Series>>

    @Query("select * from series where temporary_row = 1 order by popularity desc")
    fun getTemporary(): LiveData<List<Series>>

    @Query("UPDATE series set temporary_row = 1, watchlist = 1 where mdb_id = :mdbId")
    fun changeToPersistent(mdbId: String)

    @Query("update series set name = :name, original_name = :originalName, overview = :overview, first_air_date = :first_air_date, original_language = :original_language, poster_path = :poster_path, backdrop_path = :backdrop_path, popularity = :popularity, vote_average = :vote_average, vote_count = :vote_count where mdb_id = :mdbId")
    fun updateMain(mdbId: String, name:String, originalName: String, overview: String, first_air_date: Long, original_language: String, poster_path: String, backdrop_path: String, popularity: Double, vote_average: Double, vote_count: Int)

    @Query("update series set genres = :genres, homepage = :homepage, seasons = :number_of_seasons, status = :status, in_production = :in_production, last_air_date = :lastAirDate, networks = :networks where mdb_id = :mdbId")
    fun updateDetails(mdbId: String, genres: String, homepage: String, number_of_seasons: Int, status: String, in_production:Boolean, lastAirDate:Long, networks: String)

    @Query("select * from series where watchlist = 1 order by name asc")
    fun getWatchlist(): LiveData<List<Series>>

    @Query("select * from series where mdb_id like :mdbId limit 1")
    fun getSeries(mdbId: String): LiveData<Series>

    @Query("select * from series where mdb_id like :mdbId limit 1")
    fun getOneSeries(mdbId: String): Series?

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