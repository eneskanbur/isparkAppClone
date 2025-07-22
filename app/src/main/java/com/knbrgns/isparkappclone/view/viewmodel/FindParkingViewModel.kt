package com.knbrgns.isparkappclone.view.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.knbrgns.isparkappclone.model.Park
import com.knbrgns.isparkappclone.repository.ParkRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FindParkingViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ParkRepository()

    private val _parkList = MutableLiveData<List<Park>>()
    val parkList: MutableLiveData<List<Park>> = _parkList

    private val _favoriteIds = MutableLiveData<List<Int>>()
    val favoriteIds: MutableLiveData<List<Int>> = _favoriteIds

    private val _loading = MutableLiveData<Boolean>(false)
    val loading: MutableLiveData<Boolean> = _loading


    fun getParks() {
        viewModelScope.launch(Dispatchers.IO) {
            _loading.postValue(true)
            try {
                val result = repository.getParks()
                if (result.isSuccess) {
                    _parkList.postValue(result.getOrNull())
                }
            } catch (e: Exception) {
                // Error handling
            } finally {
                _loading.postValue(false)
            }
        }
    }


    fun loadFavorites() {
        viewModelScope.launch(Dispatchers.IO) {
            val favorites = repository.getFavoriteIds()
            _favoriteIds.postValue(favorites)
        }
    }

    fun toggleFavorite(park: Park) {

        viewModelScope.launch(Dispatchers.IO) {
            val currentFavorites = _favoriteIds.value ?: emptyList()

            if (currentFavorites.contains(park.parkID)) {
                repository.removeFromFavorites(park.parkID)
            } else {
                repository.addToFavorites(park)
            }

            loadFavorites()
        }
    }

    fun initialize() {
        if (_parkList.value.isNullOrEmpty()) {
            getParks()
        }
        loadFavorites()
    }
}