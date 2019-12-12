package by.torymo.kotlinseries.data.network

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.QueryMap
import by.torymo.kotlinseries.data.db.Series
import retrofit2.Call


interface MDBService {

    /*
        https://developers.themoviedb.org/3/tv/get-tv-details

        http://api.themoviedb.org/3/tv/<<tv_id>>?api_key=<<api_key>>&language={en-US}&append_to_response={ff}
        {
            id: integer,
            name: string,
            original_name: string,
            overview: string,
            first_air_date: string,
            origin_country: array[string],
            original_language: string,
            poster_path: string/null,
            backdrop_path: string/null,
            popularity: number,
            vote_average: number,
            vote_count: integer,

            genres: [{ id: integer, name: string}],
            homepage: string,
            in_production: boolean,
            last_air_date: string,
            networks:[{id: integer, name: string, logo_path: string, origin_country: string}],
            number_of_seasons: integer,
            production_companies:[{id: integer, name: string, logo_path: string, origin_country: string}],
            seasons:[{id: integer, name: string, air_date: string, season_number: integer, episode_count: integer, overview: string, poster_path: string}],
            status: string,
        }
     */
    @GET("/3/tv/{mdbId}")
    fun getSeriesDetails(@Path("mdbId") mdbId: Long, @QueryMap map: Map<String, String>): Call<SeriesDetailsResponse>

    /*
        https://developers.themoviedb.org/3/tv-episodes/get-tv-episode-details

        http://api.themoviedb.org/3/tv/<<tv_id>>/season/<<season_number>>?api_key=<<api_key>>&language={en-US}&append_to_response={ff}
        {
            _id: string,
            id: integer,
            name: string,
            air_date: string,
            season_number: integer,
            overview: string,
            poster_path: string,
            episodes:[{
                id: integer,
                name: string,
                air_date: string,
                episode_number: integer,
                season_number: integer,
                overview: string,
                still_path: string/null,
            }]

        }
    */
    @GET("/3/tv/{mdbId}/season/{season_number}")
    fun getSeasonDetails(@Path("mdbId") mdbId: Long, @Path("season_number") season_number: Int, @QueryMap map: Map<String, String>): Call<SeasonDetailsResponse>

    /*
        https://developers.themoviedb.org/3/search/search-tv-shows

        http://api.themoviedb.org/3/search/tv?api_key=<<api_key>>&query=<<doctor>>&language={en-US}&page={1}&first_air_date_year={2000}
        {
            page: integer,
            total_results: integer,
            total_pages: integer,
            details: [{
                id: integer,
                name: string,
                original_name: string,
                overview: string,
                first_air_date: string,
                origin_country: array[string],
                original_language: string,
                poster_path: string/null,
                backdrop_path: string/null,
                popularity: number,
                vote_average: number,
                vote_count: integer,

                genre_ids: array[integer]
            }]
        }
     */
    @GET("/3/search/tv")
    fun search(@QueryMap map: Map<String, String>): Call<SearchResponse>



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
    fun getAiringToday(@QueryMap map: Map<String, String>): Call<SearchResponse>

    /*
       Get the most newly created TV show. This is a live response and will continuously change
       http://api.themoviedb.org/tv/airing_today?api_key=6ad01c833dba757c5132002b79e99751&language=ru-en&page=1
     */
    @GET("/3/tv/top_rated")
    fun getTopRated(@QueryMap map: Map<String, String>): Call<List<Series>>
}