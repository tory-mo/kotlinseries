package by.torymo.kotlinseries.data.db


import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface SeriesDao {
    @Query("select * from series where temporary_row = 0 order by name asc")
    fun getAll(): LiveData<List<Series>>

    @Query("select * from series where temporary_row = 0 order by name asc")
    fun getList(): List<Series>

    @Query("select * from series where type = :type order by popularity desc")
    fun getByTypeOrderByPopularity(type: Int): LiveData<List<Series>>

    @Query("select * from series where type = :type order by name asc")
    fun getByTypeOrderByName(type: Int): LiveData<List<Series>>

    @Query("select * from series where name like '%' || :name  || '%' or original_name like '%' || :name  || '%' order by popularity desc")
    fun getByName(name: String): LiveData<List<Series>>

    @Query("UPDATE series set temporary_row = 0, type = 0 where id = :mdbId")
    fun changeToPersistent(mdbId: Long)

    @Query("UPDATE series set temporary_row = 1, type = 1 where id = :mdbId")
    fun changeToTemporary(mdbId: Long)

    @Update
    fun update(series: Series)

    @Query("update series set name = :name, original_name = :originalName, overview = :overview, first_air_date = :first_air_date, original_language = :original_language, poster_path = :poster_path, backdrop_path = :backdrop_path, popularity = :popularity, vote_average = :vote_average, vote_count = :vote_count where id = :mdbId")
    fun updateMain(mdbId: Long, name:String, originalName: String, overview: String, first_air_date: Long, original_language: String, poster_path: String, backdrop_path: String, popularity: Double, vote_average: Double, vote_count: Int)

    @Query("select * from series where id = :mdbId limit 1")
    fun getSeries(mdbId: Long): LiveData<Series>

    @Query("select * from series where id like :mdbId limit 1")
    fun getOneSeries(mdbId: Long): Series?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(series: Series)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(series: List<Series>)

    @Query("delete from series where temporary_row = 1 and type = :type")
    fun deleteTemporary(type: Int)

    @Query("delete from series where type_watchlist = 1 and type_airing = 0 and type_search = 0 and type_popular = 0")
    fun deleteWatchlist()

    @Query("delete from series where type_watchlist = 0 and type_airing = 1 and type_search = 0 and type_popular = 0")
    fun deleteAiring()

    @Query("delete from series where type_watchlist = 0 and type_airing = 0 and type_search = 1 and type_popular = 0")
    fun deleteSearch()

    @Query("delete from series where type_watchlist = 0 and type_airing = 0 and type_search = 0 and type_popular = 1")
    fun deletePopular()

}