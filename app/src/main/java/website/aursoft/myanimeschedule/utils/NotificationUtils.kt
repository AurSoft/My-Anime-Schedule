package website.aursoft.myanimeschedule.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import website.aursoft.myanimeschedule.BuildConfig
import website.aursoft.myanimeschedule.R

private const val NOTIFICATION_CHANNEL_ID = BuildConfig.APPLICATION_ID + ".channel"
private const val REMINDER_GROUP = BuildConfig.APPLICATION_ID + ".REMINDER_GROUP"

fun sendNotification(context: Context, animeId: Int, animeTitle: String) {
    val notificationManager = context
        .getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    // We need to create a NotificationChannel associated with our CHANNEL_ID before sending a notification.
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
        && notificationManager.getNotificationChannel(NOTIFICATION_CHANNEL_ID) == null
    ) {
        val name = context.getString(R.string.app_name)
        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            name,
            NotificationManager.IMPORTANCE_DEFAULT
        )
        notificationManager.createNotificationChannel(channel)
    }

//    build the notification object with the data to be shown
    val notification = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
        .setSmallIcon(R.mipmap.ic_launcher)
        .setContentTitle(animeTitle)
        .setContentText(context.getText(R.string.notification_msg))
        .setAutoCancel(true)
        .setGroup(REMINDER_GROUP)
        .build()

    notificationManager.notify(animeId, notification)
}