package website.aursoft.myanimeschedule.data.source

import androidx.lifecycle.LiveData
import website.aursoft.myanimeschedule.data.Anime
import website.aursoft.myanimeschedule.data.Result

interface AnimeRepository {

    fun getWeeklySchedule(): LiveData<Result<List<Anime>>>

    fun observeWatchedAnime(): LiveData<Result<List<Anime>>>

    suspend fun getAllWatchedAnime(): Result<List<Anime>>

    suspend fun getMostRecentVersionOfAnime(anime: Anime): Result<Anime>

    suspend fun watchNewAnime(anime: Anime)

    suspend fun unfollowAnime(anime: Anime)

    suspend fun updateAnimeFromRemoteDataSource()

    suspend fun updateWatchedAnime(): List<Anime>

    suspend fun deleteAnime(malId: String)
}