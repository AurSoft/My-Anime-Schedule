package website.aursoft.myanimeschedule.data.source.remote

import android.content.Context
import android.util.Log
import com.squareup.moshi.FromJson
import com.squareup.moshi.JsonReader
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.Cache
import okhttp3.OkHttpClient
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import website.aursoft.myanimeschedule.utils.WeekDaysForJikan
import website.aursoft.myanimeschedule.data.Aired
import website.aursoft.myanimeschedule.data.Anime

private val moshi = Moshi.Builder()
    .add(AnimeMoshiAdapters)
    .add(KotlinJsonAdapterFactory())
    .build()

interface JikanApiService {
    @GET("anime/{id}")
    suspend fun getAnimeById(
        @Path("id")malId: String
    ): Anime

    @GET("schedule/{day}?type=anime&subtype=airing")
    suspend fun getScheduleByDay(
        @Path("day")day: String
    ): List<Anime>
}

object JikanApi {
    const val BASE_URL = "https://api.jikan.moe/v3/"
    const val CACHE_SIZE: Long = 5 * 1024 * 1024 // 5 MB

    fun createRetrofitService(context: Context) : JikanApiService {
        // Jikan consent a good management for cached content.
        // Since the API stores every info we need for 24h, it's good to use retrofit cache
        // to avoid downloading content we already have requested
        val okHttpClient = OkHttpClient.Builder()
            .cache(Cache(context.cacheDir, CACHE_SIZE))
            .build();
        val retrofit = Retrofit.Builder()
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .build()

        return retrofit.create(JikanApiService::class.java)
    }
}
object AnimeMoshiAdapters {
    @FromJson
    fun fromJson(reader: JsonReader): List<Anime> {
        val mapOfJson = reader.readJsonValue() as Map<String, Any>
        val jsonResult = JSONObject(mapOfJson)
        var animeArray: JSONArray? = null
        var today = "monday"
        for(day in WeekDaysForJikan.values()) {
            animeArray = getAnimeJsonArray(jsonResult, day.value)
            if (animeArray != null) {
                today = day.value
                break
            }
        }
        Log.d("RemoteUtils", today)
//        Log.d("RemoteUtils", animeArray.toString())
        val animeList = mutableListOf<Anime>()
        for (i in 0 until animeArray!!.length()) {
            val animeJson = animeArray.getJSONObject(i)
            val title = animeJson.getString("title")
            val imageUrl = animeJson.getString("image_url")
            val type = animeJson.getString("type")
            val source = animeJson.getString("source")
            val aired = Aired(animeJson.getString("airing_start"), "na")
            val episodes: String? = animeJson.getString("episodes")
            val score: String? = animeJson.getString("score")
            val synopsis: String? = animeJson.getString("synopsis")
            var malId = animeJson.getString("mal_id")
            malId = malId.dropLast(2)
            val anime = Anime(
                title = title,
                imageUrl = imageUrl,
                type = type,
                source = source,
                airing = true,
                aired = aired,
                episodes = episodes?.toDoubleOrNull()?.toInt() ?: 0,
                score = score?.toDoubleOrNull() ?: 0.0,
                synopsis = synopsis ?: "",
                scheduleDay = today,
                malId = malId)
//            Log.d("RemoteUtils", anime.toString())
            animeList.add(anime)
        }

        return animeList
    }

    private fun getAnimeJsonArray(jsonResult: JSONObject, day: String) : JSONArray? {
        try {
            return jsonResult.getJSONArray(day)
        } catch(e: Exception) {
            return null
        }
    }
}