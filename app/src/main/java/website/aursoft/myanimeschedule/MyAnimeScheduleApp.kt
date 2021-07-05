package website.aursoft.myanimeschedule

import android.app.Application
import androidx.work.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module
import website.aursoft.myanimeschedule.data.source.AnimeRepository
import website.aursoft.myanimeschedule.data.source.DefaultAnimeRepository
import website.aursoft.myanimeschedule.data.source.local.AnimeLocalDataSource
import website.aursoft.myanimeschedule.data.source.local.LocalDB
import website.aursoft.myanimeschedule.data.source.remote.JikanApi
import website.aursoft.myanimeschedule.data.source.remote.RemoteDataSource
import website.aursoft.myanimeschedule.ui.AnimeDetailsViewModel
import website.aursoft.myanimeschedule.ui.home.HomeViewModel
import website.aursoft.myanimeschedule.ui.watching.WatchingViewModel
import website.aursoft.myanimeschedule.workers.RefreshCacheWorker
import website.aursoft.myanimeschedule.workers.RemoveCompletedAnimeWorker
import java.util.concurrent.TimeUnit

class MyAnimeScheduleApp : Application() {
    private val applicationScope = CoroutineScope(Dispatchers.Default)
    private fun delayedInit() = applicationScope.launch {
        setupRecurringWorkToRemoveCompletedAnime()
        setupRecurringWorkToRefreshCache()
    }

    override fun onCreate() {
        super.onCreate()

        //Declare a ViewModel - be later inject into Fragment with dedicated injector using by viewModel()
        val myModule = module {
            viewModel {
                HomeViewModel(
                    get(),
                    get() as AnimeRepository
                )
            }

            viewModel {
                WatchingViewModel(
                    get(),
                    get() as AnimeRepository
                )
            }

            viewModel {
                AnimeDetailsViewModel(
                    get(),
                    get() as AnimeRepository
                )
            }

            single { DefaultAnimeRepository(get() as RemoteDataSource, get() as AnimeLocalDataSource) as AnimeRepository}
            single { RemoteDataSource(get()) }
            single { JikanApi.createRetrofitService(this@MyAnimeScheduleApp) }
            single { AnimeLocalDataSource(get()) }
            single { LocalDB.createAnimeDAO(this@MyAnimeScheduleApp) }
        }
        startKoin {
            androidContext(this@MyAnimeScheduleApp)
            modules(listOf(myModule))
        }
        delayedInit()
    }

    // We want this work to be run once a day when connection is available
    private fun setupRecurringWorkToRemoveCompletedAnime() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        val repeatingRequest =
            PeriodicWorkRequestBuilder<RemoveCompletedAnimeWorker>(1, TimeUnit.DAYS)
                .setConstraints(constraints)
                .build()
        WorkManager.getInstance(applicationContext).enqueueUniquePeriodicWork(
            RemoveCompletedAnimeWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            repeatingRequest)
    }

    // We want this work to be run only when the device is plugged in and connected to WIFI
    private fun setupRecurringWorkToRefreshCache() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.METERED)
            .setRequiresCharging(true)
            .build()
        val repeatingRequest =
            PeriodicWorkRequestBuilder<RefreshCacheWorker>(1, TimeUnit.DAYS)
                .setConstraints(constraints)
                .build()
        WorkManager.getInstance(applicationContext).enqueueUniquePeriodicWork(
            RefreshCacheWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            repeatingRequest)
    }
}