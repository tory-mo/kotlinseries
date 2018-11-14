package by.torymo.kotlinseries

import by.torymo.kotlinseries.domain.MdbEpisodesResponse
import by.torymo.kotlinseries.domain.MdbSearchResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.QueryMap
import retrofit2.Call
import by.torymo.kotlinseries.domain.Series
import by.torymo.kotlinseries.domain.SeriesResponseResult


interface MDBService {

    /*
        http://api.themoviedb.org/3/tv/57243?api_key=6ad01c833dba757c5132002b79e99751&append_to_response=external_ids
        http://api.themoviedb.org/3/tv/57243?api_key=6ad01c833dba757c5132002b79e99751&language=ru-en&append_to_response=external_ids
        http://api.themoviedb.org/3/tv/34307?api_key=6ad01c833dba757c5132002b79e99751&language=ru-en&append_to_response=external_ids
        http://api.themoviedb.org/3/tv/1403?api_key=6ad01c833dba757c5132002b79e99751 - to get season number
     */
    @GET("/3/tv/{mdbId}")
    fun getSeries(@Path("mdbId") mdbId: String, @QueryMap map: Map<String, String>): Call<SeriesResponseResult>

    /*
        http://api.themoviedb.org/3/tv/57243?api_key=6ad01c833dba757c5132002b79e99751&append_to_response=external_ids
        http://api.themoviedb.org/3/tv/57243?api_key=6ad01c833dba757c5132002b79e99751&language=ru-en&append_to_response=external_ids
        http://api.themoviedb.org/3/tv/34307?api_key=6ad01c833dba757c5132002b79e99751&language=ru-en&append_to_response=external_ids
        http://api.themoviedb.org/3/tv/1403?api_key=6ad01c833dba757c5132002b79e99751 - to get season number
     */
    @GET("/3/tv/{mdbId}")
    fun getSeriesDetails(@Path("mdbId") mdbId: String, @QueryMap map: Map<String, String>): Call<SeriesResponseResult>

    /*
        http://api.themoviedb.org/3/tv/1403/season/5?api_key=6ad01c833dba757c5132002b79e99751&language=ru-en - episodes for a season
    */
    @GET("/3/tv/{mdbId}/season/{season_number}")
    fun getEpisodes(@Path("mdbId") mdbId: String, @Path("season_number") season_number: Long, @QueryMap map: Map<String, String>): Call<MdbEpisodesResponse>

    /*
        http://api.themoviedb.org/3/search/tv?query=%D0%B4%D0%BE%D0%BA%D1%82%D0%BE%D1%80&api_key=6ad01c833dba757c5132002b79e99751&language=ru-en
     */
    @GET("/3/search/tv")
    fun search(@QueryMap map: Map<String, String>): Call<MdbSearchResponse>

    /*
       Get the most newly created TV show. This is a live response and will continuously change
       http://api.themoviedb.org/tv/latest?api_key=6ad01c833dba757c5132002b79e99751&language=ru-en
     */
    @GET("/3/tv/latest")
    fun getLatestShows(@QueryMap map: Map<String, String>): Call<List<Series>>

    /*
       Get the most newly created TV show. This is a live response and will continuously change
       http://api.themoviedb.org/tv/airing_today?api_key=6ad01c833dba757c5132002b79e99751&language=ru-en&page=1
     */
    @GET("/3/tv/airing_today")
    fun getAiringToday(@QueryMap map: Map<String, String>): Call<MdbSearchResponse>

    /*
       Get the most newly created TV show. This is a live response and will continuously change
       http://api.themoviedb.org/tv/airing_today?api_key=6ad01c833dba757c5132002b79e99751&language=ru-en&page=1
     */
    @GET("/3/tv/top_rated")
    fun getTopRated(@QueryMap map: Map<String, String>): Call<List<Series>>
}