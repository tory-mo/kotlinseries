package by.torymo.kotlinseries.data.network

import android.util.Log
import by.torymo.kotlinseries.BuildConfig
import by.torymo.kotlinseries.data.network.Requester.Companion.APPKEY_PARAM
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
        const val LANGUAGE_PARAM = "language"
        const val QUERY_PARAM = "query"
        const val APPEND_TO_RESPONSE_PARAM = "append_to_response"
        const val PAGE_PARAM = "page"
        const val YEAR_PARAM = "first_air_date_year"

        const val LANGUAGE_EN = "en"
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

    fun getSeriesDetails(mdbId: String): SeriesDetailsResponse?{
        val map = mutableMapOf<String, String>()
        map[LANGUAGE_PARAM] = "en-US"
        map[APPEND_TO_RESPONSE_PARAM] = ""

        val call = service.getSeriesDetails(mdbId, map)
        return call.execute().body()
    }

    fun getSeasonDetails(mdbId: String, season_number: Int): SeasonDetailsResponse?{
        val map = mutableMapOf<String, String>()
        map[LANGUAGE_PARAM] = "en-US"
        map[APPEND_TO_RESPONSE_PARAM] = ""

        val call = service.getSeasonDetails(mdbId, season_number, map)
        return call.execute().body()
    }

    fun search(query: String, page: Int): SearchResponse?{
        val map = mutableMapOf<String, String>()
        map[PAGE_PARAM] = page.toString()
        map[QUERY_PARAM] = query
        map[LANGUAGE_PARAM] = "en-US"
        map[YEAR_PARAM] = 0.toString()

        val call = service.search(map)
        return call.execute().body()
    }
}

class DateTypeDeserializer : JsonDeserializer<Long?>{
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

    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): Long? {
        json?.asString?.let {
            for(i in 0..dateFormats.size){
                val regex = datePatterns[i].toRegex()
                val format = SimpleDateFormat(dateFormats[i], Locale.UK)

                if(regex.matches(it)) return format.parse(it).time
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
