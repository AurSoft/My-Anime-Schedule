package website.aursoft.myanimeschedule.workers

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.lifecycle.viewModelScope
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.launch
import retrofit2.HttpException
import website.aursoft.myanimeschedule.data.Anime
import website.aursoft.myanimeschedule.data.source.DefaultAnimeRepository
import website.aursoft.myanimeschedule.data.source.local.AnimeLocalDataSource
import website.aursoft.myanimeschedule.data.source.local.LocalDB
import website.aursoft.myanimeschedule.data.source.local.asDomainModel
import website.aursoft.myanimeschedule.data.source.remote.JikanApi
import website.aursoft.myanimeschedule.data.source.remote.RemoteDataSource
import website.aursoft.myanimeschedule.receivers.AlarmReceiver
import java.lang.Exception

class RemoveCompletedAnimeWorker(appContext: Context, params: WorkerParameters): CoroutineWorker(appContext, params) {
    companion object {
        const val WORK_NAME = "RemoveCompletedAnimeWorker"
        const val TAG = "RCAWorker"
    }

    private val alarmManager = applicationContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    override suspend fun doWork(): Result {
        Log.d(TAG, "doing work")
        val animeDao = LocalDB.createAnimeDAO(applicationContext)
        val repository = DefaultAnimeRepository(
            RemoteDataSource(JikanApi.createRetrofitService(applicationContext)),
            AnimeLocalDataSource(animeDao))
        try {
            val completedAnimeList = repository.updateWatchedAnime()
            completedAnimeList.forEach { anime ->
                cancelAlarm(anime)
                repository.deleteAnime(anime.malId)
            }
        } catch (e: HttpException) {
            Log.d(TAG, e.message())
            Result.retry()
        } catch (ex: Exception) {
            Log.d(TAG, ex.message!!)
        }
        Log.d(TAG, "success")
        return Result.success()
    }

    private fun cancelAlarm(anime: Anime) {
        val notifyPendingIntent = initPendingIntent(anime.malId, anime.title)
        alarmManager.cancel(notifyPendingIntent)
    }

    private fun initPendingIntent(animeId: String, animeTitle: String): PendingIntent {
        val notifyIntent = Intent(applicationContext, AlarmReceiver::class.java)
            notifyIntent.putExtra("ANIME_ID", animeId.toInt())
            notifyIntent.putExtra("ANIME_TITLE", animeTitle)
        return PendingIntent.getBroadcast(
            applicationContext,
            animeId.toInt(),
            notifyIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

}