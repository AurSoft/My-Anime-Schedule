package website.aursoft.myanimeschedule.ui.home

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import website.aursoft.myanimeschedule.R
import website.aursoft.myanimeschedule.base.BaseViewModel
import website.aursoft.myanimeschedule.data.Anime
import website.aursoft.myanimeschedule.data.Result
import website.aursoft.myanimeschedule.data.source.AnimeRepository
import website.aursoft.myanimeschedule.utils.WeekDaysForJikan
import website.aursoft.myanimeschedule.utils.getTodayAsString
import website.aursoft.myanimeschedule.utils.getTodayDateAsString
import java.lang.Exception
import java.net.UnknownHostException

class HomeViewModel(application: Application, private val repository: AnimeRepository) : BaseViewModel(application) {

    private val _refreshList = MutableLiveData(false)
    private val _animeList: LiveData<List<Anime>> = _refreshList.switchMap {
        showLoading.value = true
        loadScheduleFromRemote()
        repository.getWeeklySchedule().switchMap { filterAnimeList(it) }
    }
    val animeList: LiveData<List<Anime>> = _animeList

    private var currentFiltering = getTodayAsString()

    private var prefs =
        application.getSharedPreferences("website.aursoft.myanimeschedule", Context.MODE_PRIVATE)

    init {
        refreshList()
    }

    private fun filterAnimeList(result: Result<List<Anime>>): LiveData<List<Anime>> {
        val list = MutableLiveData<List<Anime>>()
        if (result is Result.Success) {
            viewModelScope.launch {
                list.value = filterAnimeByDay(result.data, currentFiltering)
            }
//            Log.d(TAG, list.value.toString())
        } else {
            showSnackBarInt.value = R.string.loading_issues
            list.value = emptyList()
        }
        showLoading.postValue(false)
        showNoData.value = list.value == null || list.value!!.isEmpty()
        return list
    }

    private fun filterAnimeByDay(animeList: List<Anime>, day: String): List<Anime> {
        return animeList.filter { anime -> anime.scheduleDay == day }
    }

    /**
     * Sets the current anime filtering type.
     *
     * @param jikanDay Can be [WeekDaysForJikan.MON], [WeekDaysForJikan.TUE], [WeekDaysForJikan.WED],
     * [WeekDaysForJikan.THU], [WeekDaysForJikan.FRI], [WeekDaysForJikan.SAT], [WeekDaysForJikan.SUN]
     */
    fun setFiltering(jikanDay: WeekDaysForJikan) {
        currentFiltering = jikanDay.value
        refreshList()
    }

    fun refreshList() {
        _refreshList.value = true
    }

    /*
     * Here we load schedule directly from remote. We use SharedPreferences to know if we already
     * downloaded schedule during the day, because Jikan updates its data in cache every 24h
     */
    private fun loadScheduleFromRemote() {
        viewModelScope.launch {
            val lastDate = loadLastUpdateDate()
            Log.d(TAG, lastDate)
            Log.d(TAG, getTodayDateAsString())
            if (lastDate != getTodayDateAsString()) {
                try {
                    repository.updateAnimeFromRemoteDataSource()
                    saveLastUpdateDate()
                    Log.d(TAG, "loaded schedule from remote")
                } catch (e: Exception) {
                    showSnackBarInt.value = R.string.loading_issues
                    Log.d(TAG, e.message!!)
                }

            }
        }
    }

    private suspend fun saveLastUpdateDate() = withContext(Dispatchers.IO) {
        prefs.edit().putString(LAST_UPDATE_DATE, getTodayDateAsString()).apply()
    }

    private suspend fun loadLastUpdateDate(): String = withContext(Dispatchers.IO) {
        prefs.getString(LAST_UPDATE_DATE, "") ?: ""
    }

    companion object {
        const val TAG = "HomeViewModel"
        const val LAST_UPDATE_DATE= "LastUpdateDate"
    }
}