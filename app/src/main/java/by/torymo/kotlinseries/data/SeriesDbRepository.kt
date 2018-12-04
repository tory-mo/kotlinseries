package by.torymo.kotlinseries.data

import android.app.Application
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
    fun getAllSeries(): LiveData<List<Series>> {
        return seriesDao.getAll()
    }

//    fun getSearchResultSeries(): List<Series> {
//        return seriesDao.getTemporary()
//    }

    fun getSearchResult(): LiveData<List<Series>> {
        return seriesDao.getTemporary()
    }

    fun clearTemporary(){
        seriesDao.deleteTemporary()
    }

    fun startFollowingSeries(mdbId: String){
        seriesDao.changeToPersistent(mdbId)
    }

    fun updateSeriesDetails(mdbId: String, genres: String, homepage: String, number_of_seasons: Int, status: String){
        seriesDao.updateDetails(mdbId, genres, homepage, number_of_seasons, status)
    }

    fun getWatchlist(): LiveData<List<Series>>{
        return seriesDao.getWatchlist()
    }

    fun getSeriesByImdbId(imdbId: String): LiveData<Series>{
        return seriesDao.getSeriesByImdbId(imdbId)
    }

    fun getSeriesByMdbId(mdbId: String): LiveData<Series>{
        return seriesDao.getSeriesByMdbId(mdbId)
    }

    fun setWatchlist(imdbId: String, watchlist: Boolean){
        seriesDao.setWatchlist(imdbId, watchlist)
    }

    fun insertSeries(series: Series){
        seriesDao.insert(series)
    }

    fun insertSeries(series: List<Series>){
        seriesDao.insert(series)
    }

    fun deleteByImdbId(imdbId: String){
        seriesDao.deleteByImdbId(imdbId)
    }

    fun deleteByMdbId(mdbId: String){
        seriesDao.deleteByMdbId(mdbId)
    }

    //episodes
    fun getAllEpisodes(): LiveData<List<Episode>>{
        return episodeDao.getAll()
    }

    fun getAllDates(): LiveData<List<Long>>{
        return episodeDao.getAllDates()
    }

    fun getEpisodesBySeries(series: String): LiveData<List<Episode>>{
        return episodeDao.getEpisodesBySeries(series)
    }

    fun getEpisodeBySeriesAndNumber(series: String, epNumber: Int, sNumber: Int): List<Episode>{
        return episodeDao.getEpisodesBySeriesAndNumber(series, epNumber, sNumber)
    }

    fun getEpisodesBetweenDates(date1: Long, date2: Long): LiveData<List<Episode>>{
        return episodeDao.getEpisodesBetweenDates(date1, date2)
    }

    fun getNotSeenEpisodesBetweenDates(date1: Long, date2: Long): LiveData<List<Episode>>{
        return episodeDao.getNotSeenEpisodesBetweenDates(date1, date2)
    }

    fun getEpisodeDatesBetweenDates(date1: Long, date2: Long): LiveData<List<Long>>{
        return episodeDao.getEpisodeDatesBetweenDates(date1, date2)
    }

    fun getNotSeenEpisodeDatesBetweenDates(date1: Long, date2: Long): LiveData<List<Long>>{
        return episodeDao.getNotSeenEpisodeDatesBetweenDates(date1, date2)
    }

    fun getEpisodesForSeriesAndDate(series: String, date: Long): LiveData<List<Episode>>{
        return episodeDao.getEpisodesForSeriesAndDate(series, date)
    }

    fun setSeen(id: Long, seen: Boolean){
        episodeDao.setSeen(id, seen)
    }

    fun insert(episode: Episode){
        episodeDao.insert(episode)
    }

    fun insert(episodes: List<Episode>){
        episodeDao.insert(episodes)
    }

    fun update(id: Long, name: String, epNumber: Int, sNumber: Int, date: Long, mPoster: String, mOverview: String){
        episodeDao.update(id, name, epNumber, sNumber, date, mPoster, mOverview)
    }

    fun delete(series: String){
        episodeDao.delete(series)
    }

    //seasons
    fun getAllSeasons(): LiveData<List<Season>>{
        return seasonDao.getAll()
    }

    fun insertSeason(season: Season){
        seasonDao.insert(season)
    }

    fun deleteSeason(imdbId: String){
        seasonDao.deleteByImdbId(imdbId)
    }
}