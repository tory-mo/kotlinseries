package by.torymo.kotlinseries.data.network

import android.util.Log
import by.torymo.kotlinseries.BuildConfig
import by.torymo.kotlinseries.data.network.Requester.Companion.APPKEY_PARAM
import by.torymo.kotlinseries.data.db.Series
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.reflect.Type
import java.text.SimpleDateFormat
import java.util.*

class Requester {
    private val service: MDBService

    companion object {
        const val BASE_URL = "http://api.themoviedb.org"
        const val APPKEY_PARAM = "api_key"
    }

    init {
        val gson = GsonBuilder()
                .registerTypeAdapter(Date::class.java, DateTypeDeserializer())
                .create()
        val client = OkHttpClient().newBuilder()
                .addInterceptor(HttpLoggingInterceptor().apply {
                    level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
                })
                .addInterceptor(ErrorInterceptor())
                .addInterceptor(ApiKeyInterceptor())
                .build()
        val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(client)
                .build()
        service = retrofit.create(MDBService::class.java)
    }

    fun getAiringToday(): MdbSearchResponse?{
        val map = mutableMapOf<String, String>()
        val call = service.getAiringToday(map)
        return call.execute().body()

    }

    fun getSeries(mdbId: String, map: Map<String, String>): SeriesResponseResult?{
        val call = service.getSeries(mdbId, map)
        return call.execute().body()
    }

    fun getSeriesDetails(mdbId: String, map: Map<String, String>): SeriesResponseResult?{
        val call = service.getSeriesDetails(mdbId, map)
        return call.execute().body()
    }

    fun getEpisodes(mdbId: String, season_number: Long, map: Map<String, String>): MdbEpisodesResponse?{
        val call = service.getEpisodes(mdbId, season_number, map)
        return call.execute().body()
    }

    fun search(map: Map<String, String>): MdbSearchResponse?{
        val call = service.search(map)
        return call.execute().body()
    }

    fun getLatestShows(map: Map<String, String>): List<Series>?{
        val call = service.getLatestShows(map)
        return call.execute().body()
    }

    fun getTopRated(map: Map<String, String>): List<Series>?{
        val call = service.getTopRated(map)
        return call.execute().body()
    }

    fun updateEpisodes(series: String, season_number: Long): MdbEpisodesResponse?{
        val call = service.getEpisodes(series, season_number, mapOf())
        return call.execute().body()

    }
}

class DateTypeDeserializer : JsonDeserializer<Date>{
    private val datePatterns = arrayOf(
        "\\d{4}-(0[1-9]|1[0-2])-(0[1-9]|[12]\\d|3[01])",
        "\\d{4}.(0[1-9]|1[0-2]).(0[1-9]|[12]\\d|3[01])",
        "(0[1-9]|[12]\\d|3[01]).(0[1-9]|1[0-2]).\\d{4}",
        "(0[1-9]|[12]\\d|3[01])-(0[1-9]|1[0-2])-\\d{4}",
        "([0-2]\\d):([0-5]\\d):([0-5]\\d)",
        ".{2,5}\\s.{2,5}\\s(0[1-9]|[12]\\d|3[01])\\s([0-2]\\d):([0-5]\\d):([0-5]\\d)\\s\\d{4}",
        "(0[1-9]|1[0-2])/(0[1-9]|[12]\\d|3[01])/\\d{4} ([0-2]\\d):([0-5]\\d):([0-5]\\d) .{2}",
        ".{2,5}\\s[1-9][0-9]?,\\s\\d{4}\\s([0-9][0-9]?):([0-5]\\d):([0-5]\\d)\\s.{2}",
        "(0[1-9]|[12]\\d|3[01]) .{2,5}. \\d{4}",
        "(0[1-9]|[12]\\d|3[01]) .{2,10} \\d{4}")

    private val dateFormats = arrayOf(
        "yyyy-MM-dd",
        "yyyy.MM.dd",
        "dd.MM.yyyy",
        "dd-MM-yyyy",
        "HH:mm:ss",
         "EEE MMM dd HH:mm:ss yyyy",
        "MM/dd/yyyy HH:mm:ss aaa",
        "MMM d, yyyy H:mm:ss a",
        "dd MMM. yyyy",
        "dd MMMM yyyy")

    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): Date? {
        json?.asString?.let {
            for(i in 0..dateFormats.size){
                val regex = datePatterns[i].toRegex()
                val format = SimpleDateFormat(dateFormats[i], Locale.UK)

                if(regex.matches(it)) return format.parse(it)
            }
        }
		return null
    }
}

class ApiKeyInterceptor: Interceptor{
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalR = chain.request()
        val originalH = originalR.url()
        val url = originalH.newBuilder()
                .addQueryParameter(APPKEY_PARAM, BuildConfig.MOVIE_DB_API_KEY)
                .build()
        val requestBuilder = originalR.newBuilder().url(url).build()
        return chain.proceed(requestBuilder)
    }

}

class ErrorInterceptor: Interceptor{
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalR = chain.request()
        val response = chain.proceed(originalR)
        return if (response.code() == 200) response
        else{
            Log.e(javaClass.name, response.code().toString())

            response
        }
    }
}
