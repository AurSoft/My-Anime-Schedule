package website.aursoft.myanimeschedule.data.source

import android.util.Log
import androidx.lifecycle.LiveData
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import website.aursoft.myanimeschedule.data.*
import website.aursoft.myanimeschedule.data.source.local.*
import website.aursoft.myanimeschedule.data.source.remote.RemoteDataSource
import website.aursoft.myanimeschedule.utils.convertBroadcastToLocale

class DefaultAnimeRepository(
    private val animeRemoteDataSource: RemoteDataSource,
    private val animeLocalDataSource: AnimeLocalDataSource,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO) : AnimeRepository {

    override fun getWeeklySchedule(): LiveData<Result<List<Anime>>> {
        return animeLocalDataSource.observeAllAnime()
    }

    override fun observeWatchedAnime(): LiveData<Result<List<Anime>>> {
        return animeLocalDataSource.observeAllWatchedAnime()
    }

    override suspend fun getAllWatchedAnime(): Result<List<Anime>> = withContext(ioDispatcher) {
        return@withContext  animeLocalDataSource.getAllWatchedAnime()
    }

    /**
     * Get the anime by id from cache if present, or directly online if absent
     * (it could be the case of an old saved anime)
     */
    override suspend fun getMostRecentVersionOfAnime(anime: Anime): Result<Anime> = withContext(ioDispatcher) {
        Log.d(TAG, anime.malId)
        val remoteResult = animeRemoteDataSource.getAnimeById(anime.malId)
        if (remoteResult is Result.Success) {
            val savedAnime = animeLocalDataSource.getAnimeById(anime.malId)
            val updatedAnime = remoteResult.data
            updatedAnime.scheduleDay = anime.scheduleDay
            updatedAnime.broadcastLocal = convertBroadcastToLocale(updatedAnime.broadcast)
            if (savedAnime is Result.Success) {
                Log.d(TAG, "saved anime found")
                Log.d(TAG, savedAnime.data.toString())
                if(updatedAnime.airing) {
                    updatedAnime.isWatching = savedAnime.data.isWatching
                    updatedAnime.isCompleted = savedAnime.data.isCompleted
                } else {
                    updatedAnime.isWatching = false
                    updatedAnime.isCompleted = false
                }
                animeLocalDataSource.updateAnime(updatedAnime)
            }
            return@withContext Result.Success(updatedAnime)
        }
        Log.d(TAG, "saved anime not found")
        return@withContext remoteResult
    }

    override suspend fun updateAnimeFromRemoteDataSource() {
        withContext(ioDispatcher) {
            val result = animeRemoteDataSource.getWeekSchedule()

            if (result is Result.Success) {
                val animeWeek = result.data
                animeLocalDataSource.deleteCache()
                for (day in animeWeek) {
                    day.value.forEach { anime ->
//                        Log.d(TAG, anime.title + " " + anime.scheduleDay)
                        val didUpdate = !animeLocalDataSource.updateAnimeInfo(anime)
                        //Log.d(TAG, anime.title + " " + didUpdate.toString())
                        if (!didUpdate) animeLocalDataSource.saveCachedAnime(anime)
                    }
                }
            } else if (result is Result.Error) {
                Log.d(TAG, result.exception.toString())
                throw result.exception
            }
        }
    }

    override suspend fun updateWatchedAnime(): List<Anime> {
        val updatedAnimeList = mutableListOf<Anime>()
        withContext(ioDispatcher) {
            val watchedAnimeResult = animeLocalDataSource.getAllWatchedAnime()
            if(watchedAnimeResult is Result.Success) {
                val animeList = watchedAnimeResult.data
                animeList.forEach{
                    val recentVersion = getMostRecentVersionOfAnime(it)
                    if(recentVersion is Result.Success) {
                        if(!recentVersion.data.airing) {
                            updatedAnimeList.add(recentVersion.data)
                        }
                    } else if (recentVersion is Result.Error) {
                        Log.d(TAG, recentVersion.exception.message.toString())
                    }
                }
            } else if (watchedAnimeResult is Result.Error) {
                throw watchedAnimeResult.exception
            }
        }

        return updatedAnimeList
    }

    override suspend fun deleteAnime(malId: String) {
        animeLocalDataSource.deleteAnime(malId)
    }

    override suspend fun watchNewAnime(anime: Anime) {
        animeLocalDataSource.watchNewAnime(anime)
    }

    override suspend fun unfollowAnime(anime: Anime) {
        animeLocalDataSource.unfollowAnime(anime)
    }

    companion object {
        const val TAG = "DefaultAnimeRep"
    }
}