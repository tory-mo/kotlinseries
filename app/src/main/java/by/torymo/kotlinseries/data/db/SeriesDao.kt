package by.torymo.kotlinseries.data.db


import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface SeriesDao {
    @Query("select * from series order by name asc")
    fun getAll(): LiveData<List<Series>>

    @Query("select * from series order by name asc")
    fun getList(): List<Series>

    @Query("select * from series where name like '%' || :name  || '%' or original_name like '%' || :name  || '%' order by popularity desc")
    fun getByName(name: String): LiveData<List<Series>>

    @Update
    fun update(series: Series)

    @Query("update series set name = :name, original_name = :originalName, overview = :overview, first_air_date = :first_air_date, original_language = :original_language, poster_path = :poster_path, backdrop_path = :backdrop_path, popularity = :popularity, vote_average = :vote_average, vote_count = :vote_count where id = :mdbId")
    fun updateMain(mdbId: Long, name:String, originalName: String, overview: String, first_air_date: Long, original_language: String, poster_path: String, backdrop_path: String, popularity: Double, vote_average: Double, vote_count: Int)

    @Query("select * from series where id = :mdbId limit 1")
    fun getSeriesLiveData(mdbId: Long): LiveData<Series>

    @Query("select * from series where id = :mdbId limit 1")
    fun getSeries(mdbId: Long): Series?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(series: Series)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(series: List<Series>)

    @Query("delete from series where id = :mdbId")
    fun delete(mdbId: Long)
}