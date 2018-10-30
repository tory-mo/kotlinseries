package by.torymo.kotlinseries.data.db

import android.app.Application
import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import by.torymo.kotlinseries.domain.Episode
import by.torymo.kotlinseries.domain.Season
import by.torymo.kotlinseries.domain.Series

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
            synchronized(lock){
                if(instance == null){
                    Room.databaseBuilder(application, SeriesDatabase::class.java, DB_NAME)
                            .allowMainThreadQueries()
                            .build()
                }
                return instance!!
            }
        }
    }
}