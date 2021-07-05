package website.aursoft.myanimeschedule.receivers

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import website.aursoft.myanimeschedule.utils.sendNotification

class AlarmReceiver: BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        sendNotification(
            context,
            intent.getIntExtra("ANIME_ID", 0),
            intent.getStringExtra("ANIME_TITLE") ?: ""
        )
    }

}