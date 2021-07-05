package website.aursoft.myanimeschedule.ui

import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.SystemClock
import android.util.Log
import android.view.View
import androidx.core.app.AlarmManagerCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import website.aursoft.myanimeschedule.R
import website.aursoft.myanimeschedule.base.BaseViewModel
import website.aursoft.myanimeschedule.data.Anime
import website.aursoft.myanimeschedule.data.Result
import website.aursoft.myanimeschedule.data.source.AnimeRepository
import website.aursoft.myanimeschedule.receivers.AlarmReceiver
import website.aursoft.myanimeschedule.utils.getBroadcastInMs

class AnimeDetailsViewModel(application: Application, private val repository: AnimeRepository) : BaseViewModel(application) {

    val selectedAnime = MutableLiveData<Anime>()
    private val _showFAB = MutableLiveData(View.GONE)
    val showFAB: LiveData<Int>
        get() = _showFAB
    private val _showUnfollowButton = MutableLiveData(View.GONE)
    val showUnfollowButton: LiveData<Int>
        get() = _showUnfollowButton
    private val _showTrailer = MutableLiveData(View.GONE)
    val showTrailer: LiveData<Int>
        get() = _showTrailer
    private val _isWatching = MutableLiveData(false)
    val isWatching: LiveData<Boolean>
        get() = _isWatching
    private var latestLoaded = false

    private val alarmManager = application.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    private val notifyIntent = Intent(application, AlarmReceiver::class.java)
    private lateinit var notifyPendingIntent: PendingIntent


    fun getMostRecentVersionOfAnime(anime: Anime) {
        if (showLoading.value == true || latestLoaded) { // If we're already loading or already loaded, return (might be a config change)
            Log.d(TAG, "Should not reload")
            return
        }
        viewModelScope.launch {
            showLoading.value = true
            val result = repository.getMostRecentVersionOfAnime(anime)
            if(result is Result.Success){
                selectedAnime.value = result.data!!
                latestLoaded = true
            } else if(result is Result.Error) {
                Log.d(TAG, result.exception.message ?: "")
                showSnackBarInt.value = R.string.not_latest_version
                selectedAnime.value = anime
            }
            toggleVisibility(selectedAnime.value!!)

            Log.d(TAG, selectedAnime.value.toString())
        }
        showLoading.postValue(false)
    }

    private fun toggleVisibility(anime: Anime) {
        if(!anime.isWatching && anime.broadcastLocal.isNotBlank()) {
            _showFAB.value = View.VISIBLE
        }
        if(anime.isWatching && anime.broadcastLocal.isNotBlank()) {
            _showUnfollowButton.value = View.VISIBLE
        }
        if(!anime.trailerUrl.isNullOrBlank()) {
            _showTrailer.value = View.VISIBLE
        }
        _isWatching.value = anime.isWatching
    }

    fun watchNewAnime(){
        //Log.d(TAG, "watchNewAnime")
        if(!selectedAnime.value!!.isWatching) {
            if (selectedAnime.value!!.broadcastLocal.isNotBlank()) {
                viewModelScope.launch {
                    repository.watchNewAnime(selectedAnime.value!!)
                }
                selectedAnime.value!!.isWatching = true
                _isWatching.value = true
                _showFAB.value = View.GONE
                _showUnfollowButton.value = View.VISIBLE
                startAlarm(
                    selectedAnime.value!!.broadcastLocal,
                    selectedAnime.value!!.malId,
                    selectedAnime.value!!.title
                )
            }
        }
    }

    fun unfollowAnime(){
        if(selectedAnime.value!!.isWatching) {
            initPendingIntent(selectedAnime.value!!.malId, selectedAnime.value!!.title)
            alarmManager.cancel(notifyPendingIntent)
            viewModelScope.launch {
                repository.unfollowAnime(selectedAnime.value!!)
            }
            selectedAnime.value!!.isWatching = false
            _isWatching.value = false
            _showFAB.value = View.VISIBLE
            _showUnfollowButton.value = View.GONE
        }
    }

    private fun initPendingIntent(animeId: String, animeTitle: String) {
        if (!this::notifyPendingIntent.isInitialized) {
            notifyIntent.putExtra("ANIME_ID", animeId.toInt())
            notifyIntent.putExtra("ANIME_TITLE", animeTitle)
            notifyPendingIntent = PendingIntent.getBroadcast(
                getApplication(),
                animeId.toInt(),
                notifyIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
        }
    }

    private fun startAlarm(broadcastLocal: String, animeId: String, animeTitle: String) {
        val triggerTime = getBroadcastInMs(broadcastLocal)
        initPendingIntent(animeId, animeTitle)
        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            triggerTime,
            AlarmManager.INTERVAL_DAY * 7,
            notifyPendingIntent
        )
    }

    companion object {
        private const val TAG = "AnimeDetailsVM"
    }
}