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

    private val _showOnlyFavorites = MutableLiveData<Boolean>(false)
    val showOnlyFavorites: MutableLiveData<Boolean> = _showOnlyFavorites

    private val _loading = MutableLiveData<Boolean>(false)
    val loading: MutableLiveData<Boolean> = _loading

    private var allParks: List<Park> = emptyList()


    init {
        repository.initDatabase(application)
    }

    fun getParks() {
        viewModelScope.launch(Dispatchers.IO) {
            _loading.postValue(true)
            try {
                val result = repository.getParks()
                if (result.isSuccess) {
                    allParks = result.getOrNull() ?: emptyList()

                    // DEBUG LOG
                    println("DEBUG: Parks loaded, count: ${allParks.size}")

                    _parkList.postValue(allParks)
                } else {
                    println("DEBUG: Failed to load parks")
                }
            } catch (e: Exception) {
                println("DEBUG: Exception: ${e.message}")
            } finally {
                _loading.postValue(false)
            }
        }
    }
    fun toggleFavorite(park: Park) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = repository.toggleFavorite(park)
                if (result.isSuccess) {
                    // Park listesini güncelle
                    val updatedParks = allParks.map {
                        if (it.parkID == park.parkID) {
                            it.copy().apply { isFavorite = result.getOrNull() ?: false }
                        } else it
                    }
                    allParks = updatedParks

                    // Gösterilen listeyi güncelle
                    if (_showOnlyFavorites.value == true) {
                        showFavoritesOnly()
                    } else {
                        _parkList.postValue(allParks)
                    }
                }
            } catch (e: Exception) {
                // Hata yönetimi
            }
        }
    }

    fun showFavoritesOnly() {
        viewModelScope.launch(Dispatchers.IO) {
            _loading.postValue(true)
            try {
                val result = repository.getFavoriteParks()
                if (result.isSuccess) {
                    _parkList.postValue(result.getOrNull() ?: emptyList())
                    _showOnlyFavorites.postValue(true)
                }
            } catch (e: Exception) {
                // Hata yönetimi
            } finally {
                _loading.postValue(false)
            }
        }
    }

    fun showAllParks() {
        _parkList.postValue(allParks)
        _showOnlyFavorites.postValue(false)
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
        if (allParks.isEmpty()) {
            getParks()
        }
    }

}