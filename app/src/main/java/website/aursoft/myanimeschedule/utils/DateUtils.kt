package website.aursoft.myanimeschedule.utils

import android.util.Log
import java.lang.Exception
import java.text.SimpleDateFormat
import java.time.*
import java.time.format.DateTimeFormatter
import java.time.format.ResolverStyle
import java.time.temporal.ChronoField
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalField
import java.util.*

const val WHOLE_DAY_NAME = "EEEE"

enum class WeekDaysForJikan(val value: String) {
    MON("monday"),
    TUE("tuesday"),
    WED("wednesday"),
    THU("thursday"),
    FRI("friday"),
    SAT("saturday"),
    SUN("sunday")
}

fun getTodayAsString(): String {
    val calendar = Calendar.getInstance()
    val currentTime = calendar.time
    val dateFormat = SimpleDateFormat(WHOLE_DAY_NAME, Locale.ENGLISH)
    return dateFormat.format(currentTime).lowercase()
}

fun getTodayDateAsString(): String {
    val formatter = DateTimeFormatter.ofPattern("dd/MM/yy")
    return LocalDateTime.now().format(formatter)
}

fun convertBroadcastToLocale(broadcast: String): String {
    Log.d("DateUtils", broadcast)
    try {
        val formatter = DateTimeFormatter.ofPattern("EEEE's' 'at' HH':'mm '(JST)'")
        val timeFormatter = DateTimeFormatter.ofPattern("EEEE's' 'at' HH':'mm '(system)'")
        val timeLocale = LocalTime.parse(broadcast, formatter)
        val dayOfWeek =
            broadcast.removeRange(broadcast.indexOf("s at"), broadcast.length).lowercase()
        val zdtOfJpn = ZonedDateTime.ofInstant(Instant.now(), ZoneId.of("JST", ZoneId.SHORT_IDS))
        val broadcastZdt = zdtOfJpn.with(ChronoField.DAY_OF_WEEK, getIntOfDay(dayOfWeek).toLong())
            .withHour(timeLocale.hour)
            .withMinute(timeLocale.minute)
            .withSecond(0)
            .withNano(0)
        Log.d("DateUtils", broadcastZdt.toString())
        val broadcastLocale =
            broadcastZdt.withZoneSameInstant(ZoneId.systemDefault()).format(timeFormatter)
        Log.d("DateUtils", broadcastLocale)
        Log.d("DateUtils", ZoneId.systemDefault().id)
        return broadcastLocale
    } catch (e: Exception) {
        Log.d("DateUtils", e.message.toString())
    }

    return ""
}

fun getBroadcastInMs(broadcast: String): Long {
    val formatter = DateTimeFormatter.ofPattern("EEEE's' 'at' HH':'mm '(system)'")
    val timeLocale = LocalTime.parse(broadcast, formatter)
    Log.d("DateUtils", broadcast)
    val dayOfWeek = broadcast.removeRange(broadcast.indexOf("s at"), broadcast.length).lowercase()
    val baseZdt = ZonedDateTime.ofInstant(Instant.now(), ZoneId.systemDefault())
    var newZdt = baseZdt.with(ChronoField.DAY_OF_WEEK, getIntOfDay(dayOfWeek).toLong())
        .withHour(timeLocale.hour)
        .withMinute(timeLocale.minute)
        .withSecond(0)
        .withNano(0)
    if(newZdt < baseZdt) {
        newZdt = newZdt.plus(1, ChronoUnit.WEEKS)
    }
    Log.d("DateUtils", newZdt.toString())
    val zdtUtc = ZonedDateTime.ofInstant(newZdt.toInstant(), ZoneId.of("UTC", ZoneId.SHORT_IDS))
    Log.d("DateUtils", zdtUtc.toString())
    val time = zdtUtc.toInstant().toEpochMilli()
    Log.d("DateUtils", time.toString())
    return time
}

private fun getIntOfDay(day: String): Int {
    return when(day) {
        "monday" -> 1
        "tuesday" -> 2
        "wednesday" -> 3
        "thursday" -> 4
        "friday" -> 5
        "saturday" -> 6
        "sunday" -> 7
        else -> -1
    }
}