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

    private val _loading = MutableLiveData<Boolean>(false)
    val loading: MutableLiveData<Boolean> = _loading

    private val _favoriteParks = MutableLiveData<List<Park>>()
    val favoriteParks: MutableLiveData<List<Park>> = _favoriteParks

    init {
        repository.initDatabase(application)
    }

    fun getParks() {
        viewModelScope.launch(Dispatchers.IO) {
            _loading.postValue(true)
            try {
                val result = repository.getParks()
                if (result.isSuccess) {
                    _parkList.postValue(result.getOrNull())
                }
            } catch (e: Exception) {

            } finally {
                _loading.postValue(false)
            }

        }
    }

    fun addToFavorites(park: Park) {

    }

    fun deleteFromFavorites(park: Park) {

    }

    fun getFavoriteParks() {
        _loading.postValue(true)
        viewModelScope.launch(Dispatchers.IO) {
            val result = repository.getFavoriteParks()
            if (result.isSuccess) {
                _favoriteParks.postValue(result.getOrNull())
            }
            _loading.postValue(false)
        }
    }

    fun getParkWithId(parkId: Int) {
        _loading.postValue(true)
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = repository.getParkWithId(parkId)
                if (result.isSuccess && result.getOrNull() != null) {
                    _parkList.postValue(listOf(result.getOrNull() as Park))
                }
            } catch (e: Exception) {

            } finally {
                _loading.postValue(false)
            }
        }
    }

    fun initialize() {
        if (_parkList.value.isNullOrEmpty()) {
            getParks()
        }
    }

}