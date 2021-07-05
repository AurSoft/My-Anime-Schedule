package website.aursoft.myanimeschedule.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import retrofit2.HttpException
import website.aursoft.myanimeschedule.data.source.DefaultAnimeRepository
import website.aursoft.myanimeschedule.data.source.local.AnimeLocalDataSource
import website.aursoft.myanimeschedule.data.source.local.LocalDB
import website.aursoft.myanimeschedule.data.source.remote.JikanApi
import website.aursoft.myanimeschedule.data.source.remote.RemoteDataSource

class RefreshCacheWorker(appContext: Context, params: WorkerParameters): CoroutineWorker(appContext, params) {
    companion object {
        const val WORK_NAME = "RefreshCacheWorker"
        const val TAG = "RefreshCacheWorker"
    }

    override suspend fun doWork(): Result {
        val animeDao = LocalDB.createAnimeDAO(applicationContext)
        val repository = DefaultAnimeRepository(
            RemoteDataSource(JikanApi.createRetrofitService(applicationContext)),
            AnimeLocalDataSource(animeDao))
        try {
            repository.updateAnimeFromRemoteDataSource()
        } catch (e: HttpException) {
            Result.retry()
        }
        return Result.success()
    }

}