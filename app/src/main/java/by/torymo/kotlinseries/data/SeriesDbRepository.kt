package by.torymo.kotlinseries.data

import android.app.Application
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData

import by.torymo.kotlinseries.data.db.EpisodeDao
import by.torymo.kotlinseries.data.db.SeasonDao
import by.torymo.kotlinseries.data.db.SeriesDao
import by.torymo.kotlinseries.data.db.SeriesDatabase
import by.torymo.kotlinseries.data.db.Episode
import by.torymo.kotlinseries.data.db.Season
import by.torymo.kotlinseries.data.db.Series

class SeriesDbRepository(application: Application) {
    private val seriesDao: SeriesDao
    private val episodeDao: EpisodeDao
    private val seasonDao: SeasonDao

    init{
        val seriesDb = SeriesDatabase.getInstance(application)
        seriesDao = seriesDb.seriesDao()
        episodeDao = seriesDb.episodeDao()
        seasonDao = seriesDb.seasonDao()
    }

    //series
    fun oneSeries(mdbId: String): Series? = seriesDao.getOneSeries(mdbId)

    fun getAllSeries(): LiveData<List<Series>> = seriesDao.getAll()

    fun getByName(name: String): LiveData<List<Series>> = if(name.isEmpty()) seriesDao.getAll() else seriesDao.getByName(name)

    fun clearTemporary() = seriesDao.deleteTemporary()

    fun startFollowingSeries(mdbId: String) = seriesDao.changeToPersistent(mdbId)

    fun updateSeriesDetails(mdbId: String, genres: String, homepage: String, number_of_seasons: Int, status: String, in_production:Boolean, lastAirDate:Long, networks: String){
        seriesDao.updateDetails(mdbId, genres, homepage, number_of_seasons, status, in_production, lastAirDate, networks)
    }

    fun updateSeriesMain(mdbId: String, name:String, originalName: String, overview: String, first_air_date: Long, original_language: String, poster_path: String, backdrop_path: String, popularity: Double, vote_average: Double, vote_count: Int){
        seriesDao.updateMain(mdbId, name, originalName, overview, first_air_date, original_language, poster_path, backdrop_path, popularity, vote_average, vote_count)
    }

    fun getWatchlist(): LiveData<List<Series>> = seriesDao.getWatchlist()


    fun getSeries(mdbId: String): LiveData<Series> = seriesDao.getSeries(mdbId)

    fun setWatchlist(imdbId: String, watchlist: Boolean) = seriesDao.setWatchlist(imdbId, watchlist)

    @WorkerThread
    fun insertSeries(series: Series) = seriesDao.insert(series)

    @WorkerThread
    fun insertSeries(series: List<Series>) = seriesDao.insert(series)

    fun deleteSeries(mdbId: String) = seriesDao.delete(mdbId)

    //episodes
    fun getAllEpisodes(): LiveData<List<Episode>> = episodeDao.getAll()

    fun getAllDates(): LiveData<List<Long>> = episodeDao.getAllDates()

    fun getEpisodesBySeries(series: String): LiveData<List<Episode>> = episodeDao.getEpisodesBySeries(series)

    fun getEpisodeBySeriesAndNumber(series: String, epNumber: Int, sNumber: Int): List<Episode> = episodeDao.getEpisodesBySeriesAndNumber(series, epNumber, sNumber)

    fun getEpisodesBetweenDates(date1: Long, date2: Long): LiveData<List<Episode>> = episodeDao.getEpisodesBetweenDates(date1, date2)

    fun getNotSeenEpisodesBetweenDates(date1: Long, date2: Long): LiveData<List<Episode>> = episodeDao.getNotSeenEpisodesBetweenDates(date1, date2)

    fun getEpisodeDatesBetweenDates(date1: Long, date2: Long): LiveData<List<Long>> = episodeDao.getEpisodeDatesBetweenDates(date1, date2)

    fun getNotSeenEpisodeDatesBetweenDates(date1: Long, date2: Long): LiveData<List<Long>> = episodeDao.getNotSeenEpisodeDatesBetweenDates(date1, date2)

    fun getEpisodesForSeriesAndDate(series: String, date: Long): LiveData<List<Episode>> = episodeDao.getEpisodesForSeriesAndDate(series, date)

    fun setSeen(id: Long, seen: Boolean) = episodeDao.setSeen(id, seen)

    @WorkerThread
    fun insert(episode: Episode) = episodeDao.insert(episode)

    @WorkerThread
    fun insert(episodes: List<Episode>) = episodeDao.insert(episodes)

    fun update(id: Long, name: String, epNumber: Int, sNumber: Int, date: Long, mPoster: String, mOverview: String){
        episodeDao.update(id, name, epNumber, sNumber, date, mPoster, mOverview)
    }

    fun delete(series: String) = episodeDao.delete(series)

    //seasons
    fun getAllSeasons(): LiveData<List<Season>> = seasonDao.getAll()

    @WorkerThread
    fun insertSeason(season: Season) = seasonDao.insert(season)

    fun deleteSeason(mdbId: String) = seasonDao.delete(mdbId)
}