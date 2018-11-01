package by.torymo.kotlinseries.data.db

import android.app.Application
import android.arch.persistence.db.SupportSQLiteDatabase
import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.os.AsyncTask
import by.torymo.kotlinseries.SeriesTestDataProvider
import by.torymo.kotlinseries.domain.Episode
import by.torymo.kotlinseries.domain.Season
import by.torymo.kotlinseries.domain.Series

@Database(entities = [Episode::class, Series::class, Season::class], version = 1)
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