package by.torymo.kotlinseries

import by.torymo.kotlinseries.Requester.Companion.APPKEY_PARAM
import by.torymo.kotlinseries.domain.Series
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Callback
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.reflect.Type
import java.util.*

class Requester {
    private val service: MDBService

    companion object {
        const val BASE_URL = "http://api.themoviedb.org"
        const val APPKEY_PARAM = "api_key"
    }

    init {
        val logginInterceptor = HttpLoggingInterceptor()
        logginInterceptor.level = HttpLoggingInterceptor.Level.BODY

        val gson = GsonBuilder()
                .registerTypeAdapter(Date::class.java, DateTypeDeserializer())
                .create()
        val client = OkHttpClient.Builder()
                .addInterceptor(logginInterceptor)
                .addInterceptor(ApiKeyInterceptor())
                .build()
        val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(client)
                .build()
        service = retrofit.create(MDBService::class.java)
    }

    fun getAiringToday(callback: Callback<List<Series>>, map: Map<String, String>){
        val call = service.getAiringToday(map)
        call.enqueue(callback)
    }
}

class DateTypeDeserializer : JsonDeserializer<Date>{
    private val DATE_FORMATS = arrayOf("yyyy-MM-dd", "yyyy-MM-dd'T'HH:mm:ss", "yyyy-MM-dd'T'HH:mm:ssZ", "EEE MMM dd HH:mm:ss z yyyy", "HH:mm:ss", "MM/dd/yyyy HH:mm:ss aaa", "yyyy-MM-dd'T'HH:mm:ss.SSSSSS", "yyyy-MM-dd'T'HH:mm:ss.SSSSSSS", "yyyy-MM-dd'T'HH:mm:ss.SSSSSSS'Z'", "MMM d',' yyyy H:mm:ss a", "dd MMM. yyyy", "dd MMMM yyyy", "")
    private val DATE_PATTERN = arrayOf(
            "\\d{4}-(0[1-9]|1[0-2])-(0[1-9]|[12]\\d|3[01])",
            "yyyy-MM-dd'T'HH:mm:ss",
            "yyyy-MM-dd'T'HH:mm:ssZ",
            "EEE MMM dd HH:mm:ss z yyyy",
            "HH:mm:ss",
            "MM/dd/yyyy HH:mm:ss aaa",
            "yyyy-MM-dd'T'HH:mm:ss.SSSSSS",
            "yyyy-MM-dd'T'HH:mm:ss.SSSSSSS",
            "yyyy-MM-dd'T'HH:mm:ss.SSSSSSS'Z'",
            "MMM d',' yyyy H:mm:ss a",
            "dd MMM. yyyy",
            "dd MMMM yyyy")
    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): Date {
        var date = Date()
        json.
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