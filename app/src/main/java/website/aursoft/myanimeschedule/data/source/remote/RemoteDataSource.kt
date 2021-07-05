package website.aursoft.myanimeschedule.data.source.remote

import android.content.Context
import android.util.Log
import website.aursoft.myanimeschedule.utils.WeekDaysForJikan
import website.aursoft.myanimeschedule.data.Anime
import website.aursoft.myanimeschedule.data.Result
import website.aursoft.myanimeschedule.data.Result.Success
import website.aursoft.myanimeschedule.data.Result.Error

class RemoteDataSource(private val jikanService: JikanApiService) {
    companion object {
        private const val TAG = "RemoteDataSource"
    }

    /**
     * Get all week schedule, divided by day.
     */
    suspend fun getWeekSchedule(): Result<Map<String, List<Anime>>> {
        val mutMap = mutableMapOf<String, List<Anime>>()
        for (day in WeekDaysForJikan.values()) {
            Log.d("RemoteDataSource", day.value)
            val res = getScheduleByDay(day.value)
            if (res is Success) {
                //Log.d("RemoteDataSource", res.data.toString())
                mutMap[day.value] = res.data
            } else {
                res as Error
                return Error(res.exception)
            }
        }
        return Success(mutMap)
    }

    private suspend fun getScheduleByDay(day: String) : Result<List<Anime>> {
        return try {
            val daySchedule = jikanService.getScheduleByDay(day)
            return Success(daySchedule)
        } catch (e: Exception) {
            Error(e)
        }
    }

    suspend fun getAnimeById(id: String): Result<Anime> {
        return try {
            Success(jikanService.getAnimeById(id))
        } catch(e: Exception) {
            Error(e)
        }
    }
}