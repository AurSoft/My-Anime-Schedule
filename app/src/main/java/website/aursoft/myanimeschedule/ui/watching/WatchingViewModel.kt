package website.aursoft.myanimeschedule.ui.watching

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import website.aursoft.myanimeschedule.R
import website.aursoft.myanimeschedule.base.BaseViewModel
import website.aursoft.myanimeschedule.data.Anime
import website.aursoft.myanimeschedule.data.Result
import website.aursoft.myanimeschedule.data.source.AnimeRepository

class WatchingViewModel(application: Application, private val repository: AnimeRepository) : BaseViewModel(application) {

    private val _animeList: LiveData<List<Anime>> = repository.observeWatchedAnime().switchMap {
        showLoading.value = true
        filterAnimeList(it)
    }
    val animeList: LiveData<List<Anime>> = _animeList

    private fun filterAnimeList(result: Result<List<Anime>>): LiveData<List<Anime>> {
        val list = MutableLiveData<List<Anime>>()
        if (result is Result.Success) {
            list.value = result.data!!
            Log.d(TAG, list.value.toString())
        } else {
            showSnackBarInt.value = R.string.loading_issues_watching
            list.value = emptyList()
        }
        showLoading.postValue(false)
        showNoData.value = list.value == null || list.value!!.isEmpty()
        return list
    }
    companion object {
        const val TAG = "WatchingViewModel"
    }
}