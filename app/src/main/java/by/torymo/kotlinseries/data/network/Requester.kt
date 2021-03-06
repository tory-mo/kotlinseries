package by.torymo.kotlinseries.data.network

import android.util.Log
import by.torymo.kotlinseries.BuildConfig
import by.torymo.kotlinseries.DateTimeUtils
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
import java.lang.Exception
import java.lang.reflect.Type
import java.util.*

class Requester {

    private val service: MDBService

    companion object {
        const val POSTER_PATH = "http://image.tmdb.org/t/p/w500/"

        const val BASE_URL = "http://api.themoviedb.org"
        const val APPKEY_PARAM = "api_key"
        const val LANGUAGE_PARAM = "language"
        const val QUERY_PARAM = "query"
        const val APPEND_TO_RESPONSE_PARAM = "append_to_response"
        const val PAGE_PARAM = "page"
        const val YEAR_PARAM = "first_air_date_year"
        const val TIMEZONE_PARAM = "timezone"

        const val LANGUAGE_EN = "en"
    }

    init {
        val gson = GsonBuilder()
                .registerTypeAdapter(Long::class.java, DateTypeDeserializer())
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

    suspend fun getSeriesDetails(mdbId: Long): SeriesDetailsResponse{
        val map = mutableMapOf<String, String>()
        map[LANGUAGE_PARAM] = getLanguage()
        map[APPEND_TO_RESPONSE_PARAM] = "content_ratings,aggregate_credits"

        return service.getSeriesDetails(mdbId, map)
    }

    fun getSeasonDetails(mdbId: Long, season_number: Int): SeasonDetailsResponse?{
        val map = mutableMapOf<String, String>()
        map[LANGUAGE_PARAM] = getLanguage()
        map[APPEND_TO_RESPONSE_PARAM] = ""

        val call = service.getSeasonDetails(mdbId, season_number, map)
        return call.execute().body()
    }

    suspend fun search(query: String, page: Int): SearchResponse{
        val map = mutableMapOf<String, String>()
        map[PAGE_PARAM] = page.toString()
        map[QUERY_PARAM] = query
        map[LANGUAGE_PARAM] = getLanguage()
        map[YEAR_PARAM] = 0.toString()

        return service.search(map)
    }

    suspend fun airingToday(page: Int): SearchResponse{
        val map = mutableMapOf<String, String>()
        map[PAGE_PARAM] = page.toString()
        map[LANGUAGE_PARAM] = getLanguage()
        map[TIMEZONE_PARAM] = DateTimeUtils.timezone()

        return service.getAiringToday(map)
    }

    suspend fun popular(page: Int): SearchResponse{
        val map = mutableMapOf<String, String>()
        map[PAGE_PARAM] = page.toString()
        map[LANGUAGE_PARAM] = getLanguage()

        return service.getPopular(map)
    }

    private fun getLanguage(): String{
        val currLanguage = Locale.getDefault().language
        var needLang = LANGUAGE_EN
        if (currLanguage != needLang) {
            needLang = "$currLanguage-$LANGUAGE_EN"
        }
        return needLang
    }
}

class DateTypeDeserializer : JsonDeserializer<Long>{
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

    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): Long {
        try {
            json?.asString?.let {
                for (i in dateFormats.indices) {
                    val regex = datePatterns[i].toRegex()
                    if (regex.matches(it)) {
                        return DateTimeUtils.toMilliseconds(it, dateFormats[i])
                    }
                }
            }
        }catch (exception: Exception){
            exception.printStackTrace()
        }
		return 0
    }
}

class ApiKeyInterceptor: Interceptor{
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalR = chain.request()
        val originalH = originalR.url
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
        return if (response.code == 200) response
        else{
            Log.e(javaClass.name, response.code.toString())

            response
        }
    }
}
