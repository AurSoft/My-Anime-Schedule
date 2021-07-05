package website.aursoft.myanimeschedule.receivers

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.JobIntentService
import kotlinx.coroutines.*
import org.koin.android.ext.android.inject
import website.aursoft.myanimeschedule.data.source.AnimeRepository
import website.aursoft.myanimeschedule.data.Result
import website.aursoft.myanimeschedule.utils.getBroadcastInMs
import kotlin.coroutines.CoroutineContext

/**
 * Because alarms disappears after boot, we need a way to restart them
 */
class RestartAlarmsJobIntentService : JobIntentService(), CoroutineScope {
    private var coroutineJob: Job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + coroutineJob

    companion object {
        private const val JOB_ID = 379
        private const val TAG = "RestartAlarmsJIS"

        // call this to start the JobIntentService to restart alarms
        fun enqueueWork(context: Context, intent: Intent) {
            enqueueWork(
                context,
                RestartAlarmsJobIntentService::class.java, JOB_ID,
                intent
            )
        }
    }

    override fun onHandleWork(intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            val repository: AnimeRepository by inject()
            CoroutineScope(coroutineContext).launch(SupervisorJob()) {
                val result = repository.getAllWatchedAnime()
                if (result is Result.Success) {
                    val watchingAnimeList = result.data
                    watchingAnimeList.forEach {
                        val alarmManager = applicationContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                        startAlarm(applicationContext, alarmManager, it.broadcastLocal, it.malId, it.title)
                    }
                }
            }
        }
    }

    private fun startAlarm(context: Context, alarmManager: AlarmManager,
                           broadcastLocal: String, animeId: String, animeTitle: String) {
        val triggerTime = getBroadcastInMs(broadcastLocal)
        val notifyPendingIntent = initPendingIntent(context, animeId, animeTitle)
        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            triggerTime,
            AlarmManager.INTERVAL_DAY * 7,
            notifyPendingIntent
        )
    }

    private fun initPendingIntent(context: Context, animeId: String, animeTitle: String): PendingIntent {
        val notifyIntent = Intent(context, AlarmReceiver::class.java)
        notifyIntent.putExtra("ANIME_ID", animeId.toInt())
        notifyIntent.putExtra("ANIME_TITLE", animeTitle)
        return PendingIntent.getBroadcast(
            context,
            animeId.toInt(),
            notifyIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
    }
}