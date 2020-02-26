package by.torymo.kotlinseries.data

import android.app.Application
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import by.torymo.kotlinseries.data.db.*
import by.torymo.kotlinseries.data.network.EpisodeResponse

import by.torymo.kotlinseries.data.network.Seasons
import by.torymo.kotlinseries.data.network.SeriesDetailsResponse

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
    fun getSeriesList(): List<Series> = seriesDao.getList()

    fun getSeriesByType(type: SeriesRepository.Companion.SeriesType): LiveData<List<Series>> = seriesDao.getByType(type.type)

    fun getByName(name: String): LiveData<List<Series>> = if(name.isEmpty()) seriesDao.getAll() else seriesDao.getByName(name)

    fun clearTemporary(type: SeriesRepository.Companion.SeriesType = SeriesRepository.Companion.SeriesType.SEARCH_RESULT) = seriesDao.deleteTemporary(type.type)

    fun startFollowingSeries(mdbId: Long) = seriesDao.changeToPersistent(mdbId)

    fun stopFollowingSeries(mdbId: Long) = seriesDao.changeToTemporary(mdbId)

    fun insertOrUpdateSeriesMainInfo(seriesResponse: SeriesDetailsResponse, type: SeriesRepository.Companion.SeriesType = SeriesRepository.Companion.SeriesType.SEARCH_RESULT){
        val existingSeries = seriesDao.getOneSeries(seriesResponse.id.toLong())
        if(existingSeries == null) {
            val tmpSeries = seriesResponse.toSeries()
            tmpSeries.type = type.type
            seriesDao.insert(tmpSeries)
        }else{
            seriesDao.updateMain(mdbId = seriesResponse.id.toLong(),
                    name = seriesResponse.name,
                    originalName = seriesResponse.original_name,
                    overview = seriesResponse.overview,
                    first_air_date = seriesResponse.first_air_date,
                    original_language = seriesResponse.original_language,
                    poster_path = seriesResponse.poster_path ?: existingSeries.poster,
                    backdrop_path = seriesResponse.backdrop_path ?: existingSeries.backdrop,
                    popularity = seriesResponse.popularity,
                    vote_average = seriesResponse.vote_average,
                    vote_count = seriesResponse.vote_count)
        }
    }

    fun updateSeriesDetails(seriesResponse: SeriesDetailsResponse){
        val existingSeries = seriesDao.getOneSeries(seriesResponse.id.toLong()) ?: return

        val tmpSeries = seriesResponse.toSeries()
        tmpSeries.temporary = existingSeries.temporary
        tmpSeries.type = existingSeries.type

        val genres = seriesResponse.genres.map { genre ->  genre.name }.toString()
        tmpSeries.genres = if(genres.isEmpty()) tmpSeries.genres else genres.substring(1, genres.length-1)
        val networks = seriesResponse.networks.map { network ->  network.name }.toString()
        tmpSeries.networks = if(networks.isEmpty()) tmpSeries.networks else networks.substring(1, networks.length-1)

        seriesDao.update(tmpSeries)
    }

    fun getSeries(mdbId: Long): LiveData<Series> = seriesDao.getSeries(mdbId)

    //episodes
    fun getEpisodesBySeries(series: Long): LiveData<List<ExtendedEpisode>> = episodeDao.getEpisodesBySeries(series)

    fun insertOrUpdateEpisode(series: Long, season: Long, episodeResponse: EpisodeResponse){
        val episode = episodeDao.getEpisodesBySeasonAndNumber(season, episodeResponse.episode_number)
        val episodeTmp = episodeResponse.toEpisode(series, season)
        if (episode == null) {
            episodeDao.insert(episodeTmp)
        }else{
            episodeTmp.id = episode.id
            episodeTmp.seen = episode.seen
            episodeDao.update(episodeTmp)
        }
    }

    fun getEpisodesForDate(date: Long, flag: SeriesRepository.Companion.EpisodeStatus = SeriesRepository.Companion.EpisodeStatus.ALL): List<ExtendedEpisode>{
        return when(flag){
            SeriesRepository.Companion.EpisodeStatus.NOT_SEEN -> episodeDao.getNotSeenEpisodesForDate(date)
            else -> episodeDao.getEpisodesForDate(date)
        }
    }

    fun getEpisodeDatesBetweenDates(date1: Long, date2: Long, flag: SeriesRepository.Companion.EpisodeStatus): List<Long>{
        return when(flag){
            SeriesRepository.Companion.EpisodeStatus.NOT_SEEN -> episodeDao.getNotSeenEpisodeDatesBetweenDates(date1, date2)
            else -> episodeDao.getEpisodeDatesBetweenDates(date1, date2)
        }
    }

    fun setSeen(id: Long, seen: Boolean) = episodeDao.setSeen(id, seen)

    fun deleteEpisodesBySeries(series: Long) = episodeDao.deleteBySeries(series)
    fun deleteEpisodesBySeason(season: Long) = episodeDao.deleteBySeason(season)

    //seasons

    fun getSeasons(series: Long): LiveData<List<Season>> = seasonDao.get(series)

    @WorkerThread
    fun getFollowingOrLast(series: Long): List<Season>{
        val seasonsFromDb = seasonDao.getFollowing(series)
        if(seasonsFromDb.isEmpty()){
            val tmp = seasonDao.getLast(series)
            tmp?.let {
                tmp.following = true
                seasonDao.updateFollowing(tmp.id, true)
                return listOf(tmp)
            }
        }
        return seasonsFromDb
    }

    @WorkerThread
    fun insertOrUpdateSeasons(series: Long, seasonsResponse: List<Seasons>){
        val insertSeasons = mutableListOf<Season>()
        val updateSeasons = mutableListOf<Season>()

        for(season: Seasons in seasonsResponse){
            val tmp = season.toDbSeason(series)
            val inDb = seasonDao.getById(tmp.id)
            if(inDb == null)
                insertSeasons.add(tmp)
            else {
                tmp.following = inDb.following
                updateSeasons.add(tmp)
            }
        }
        if(insertSeasons.isNotEmpty()) seasonDao.insert(insertSeasons)
        if(updateSeasons.isNotEmpty()) seasonDao.update(updateSeasons)
    }

    @WorkerThread
    fun updateSeasonFollowing(season: Long, following: Boolean) = seasonDao.updateFollowing(season, following)
}