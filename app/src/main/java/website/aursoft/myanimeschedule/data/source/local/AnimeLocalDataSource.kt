package website.aursoft.myanimeschedule.data.source.local

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.map
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import website.aursoft.myanimeschedule.data.*
import website.aursoft.myanimeschedule.data.Result.Success
import website.aursoft.myanimeschedule.data.Result.Error

class AnimeLocalDataSource internal constructor(
    private val animeDAO: AnimeDAO,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    fun observeAllAnime(): LiveData<Result<List<Anime>>> {
        val result = Transformations.map(animeDAO.observeAllAnime()) { list ->
            list.asDomainModel()
        }
        return result.map{ Success(it) }
    }

    fun observeAllWatchedAnime(): LiveData<Result<List<Anime>>> {
        val result = Transformations.map(animeDAO.observeAllWatchedAnime()) { list ->
            list.asDomainModel()
        }
        return result.map{ Success(it) }
    }

    suspend fun deleteCache() = withContext(ioDispatcher) {
        animeDAO.deleteCache()
    }

    suspend fun deleteAnime(malId: String) = withContext(ioDispatcher) {
        animeDAO.deleteAnime(malId)
    }

    suspend fun saveCachedAnime(anime: Anime) = withContext(ioDispatcher) {
        animeDAO.saveAnime(anime.asAnimeDTO())
    }

    suspend fun updateAnimeInfo(anime: Anime): Boolean = withContext(ioDispatcher) {
        return@withContext animeDAO.updateAnime(anime.asCachedAnimeDTO()) != -1
    }

    suspend fun updateAnime(anime: Anime): Boolean = withContext(ioDispatcher) {
        Log.d(TAG, anime.toString())
        Log.d(TAG, anime.asAnimeDTO().toString())
        return@withContext animeDAO.updateAnime(anime.asAnimeDTO()) != -1
    }

    suspend fun watchNewAnime(anime: Anime) = withContext(ioDispatcher) {
        val savedAnime = anime.asAnimeDTO()
        savedAnime.isWatching = true
        Log.d(TAG, savedAnime.toString())
        Log.d(TAG, animeDAO.updateAnime(savedAnime).toString())
    }

    suspend fun unfollowAnime(anime: Anime) = withContext(ioDispatcher) {
        val savedAnime = anime.asAnimeDTO()
        savedAnime.isWatching = false
        Log.d(TAG, savedAnime.toString())
        Log.d(TAG, animeDAO.updateAnime(savedAnime).toString())
    }

    suspend fun getAnimeById(id: String): Result<Anime> = withContext(ioDispatcher) {
        try {
            val anime = animeDAO.getAnimeById(id)?.asDomainModel()
            if (anime != null) {
                return@withContext Success(anime)
            } else {
                return@withContext Error(Exception("Anime not found!"))
            }
        } catch (e: Exception) {
            return@withContext Error(e)
        }
    }

    suspend fun getAllWatchedAnime(): Result<List<Anime>> = withContext(ioDispatcher) {
        try {
            val animeList = animeDAO.getAllWatchedAnime().asDomainModel()
            if (!animeList.isNullOrEmpty()) {
                return@withContext Success(animeList)
            } else {
                return@withContext Error(Exception("List not found!"))
            }
        } catch (e: Exception) {
            return@withContext Error(e)
        }
    }

    companion object {
        private const val TAG = "AnimeLocalDS"
    }
}