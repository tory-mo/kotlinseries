package by.torymo.kotlinseries.data.db

import android.app.Application
import android.os.AsyncTask
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import by.torymo.kotlinseries.SeriesTestDataProvider

@Database(entities = [Episode::class, Series::class, Season::class], version = 1, exportSchema = false)
abstract class SeriesDatabase : RoomDatabase(){
    abstract fun episodeDao(): EpisodeDao
    abstract fun seriesDao(): SeriesDao
    abstract fun seasonDao(): SeasonDao

    companion object {
        private val lock = Any()
        private const val DB_NAME = "Series.db"
        private var instance: SeriesDatabase? = null

        fun getInstance(application: Application): SeriesDatabase{
            synchronized(SeriesDatabase.lock){
                if(SeriesDatabase.instance == null){
                    SeriesDatabase.instance = Room.databaseBuilder(application, SeriesDatabase::class.java, SeriesDatabase.DB_NAME)
                            .allowMainThreadQueries()
                            .addCallback(object : RoomDatabase.Callback() {
                                override fun onCreate(db: SupportSQLiteDatabase) {
                                    super.onCreate(db)
                                    SeriesDatabase.instance?.let {
                                        SeriesDatabase.prePopulate(it, SeriesTestDataProvider.seriesList, SeriesTestDataProvider.episodesList)
                                    }
                                }
                            })
                            .build()
                }
                return SeriesDatabase.instance!!
            }
        }

        fun prePopulate(database: SeriesDatabase, seriesList: List<Series>, episodeList: List<Episode>) {
            AsyncTask.execute { database.seriesDao().insert(seriesList)}
            AsyncTask.execute { database.episodeDao().insert(episodeList)}

        }
    }
}