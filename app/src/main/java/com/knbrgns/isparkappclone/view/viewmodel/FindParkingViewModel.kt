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

    // ✅ TEK ITEM GÜNCELLEMESİ İÇİN
    private val _favoriteUpdate = MutableLiveData<Pair<Int, Boolean>>()
    val favoriteUpdate: MutableLiveData<Pair<Int, Boolean>> = _favoriteUpdate

    private var allParks: MutableList<Park> = mutableListOf()

    init {
        repository.initDatabase(application)
    }

    fun getParks() {
        viewModelScope.launch(Dispatchers.IO) {
            _loading.postValue(true)
            try {
                val result = repository.getParks()
                if (result.isSuccess) {
                    allParks.clear()
                    allParks.addAll(result.getOrNull() ?: emptyList())
                    refreshDisplayList()
                }
            } catch (e: Exception) {
                // Handle error
            } finally {
                _loading.postValue(false)
            }
        }
    }

    fun toggleFavorite(park: Park, position: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = repository.toggleFavorite(park)
            if (result.isSuccess) {
                val newState = result.getOrNull() ?: false

                // Master listeyi güncelle
                updateParkInList(park.parkID, newState)

                if (_showOnlyFavorites.value == true && !newState) {
                    // Favorilerden çıkarıldı - listeyi yenile
                    refreshDisplayList()
                } else {
                    // Sadece tek item güncelle
                    _favoriteUpdate.postValue(Pair(position, newState))
                }
            }
        }
    }

    fun showFavoritesOnly() {
        _showOnlyFavorites.postValue(true)
        refreshDisplayList()
    }

    fun showAllParks() {
        _showOnlyFavorites.postValue(false)
        refreshDisplayList()
    }

    fun initialize() {
        println("DEBUG: initialize called - allParks.size: ${allParks.size}")
        if (allParks.isEmpty()) {
            getParks()
        } else {
            // ✅ Data varsa tekrar göster
            restoreDisplay()
        }
    }

    // ✅ GERİ GELİNDİĞİNDE RESTORE
    fun restoreDisplay() {
        println("DEBUG: restoreDisplay called - allParks.size: ${allParks.size}")
        if (allParks.isNotEmpty()) {
            refreshDisplayList()
        } else {
            // ✅ Eğer allParks boşsa tekrar yükle
            println("DEBUG: allParks empty, reloading...")
            getParks()
        }
    }

    // ✅ SADECE 2 HELPER FUNCTION
    private fun refreshDisplayList() {
        println("DEBUG: refreshDisplayList called - Favorites mode: ${_showOnlyFavorites.value}")
        viewModelScope.launch(Dispatchers.IO) {
            val displayList = if (_showOnlyFavorites.value == true) {
                val favorites = repository.getFavoriteParks()
                favorites.getOrNull()?.map { it.copy(isFavorite = true) } ?: emptyList()
            } else {
                allParks.toList()
            }
            println("DEBUG: Posting ${displayList.size} items to parkList")
            _parkList.postValue(displayList)
        }
    }

    private fun updateParkInList(parkID: Int, newState: Boolean) {
        val index = allParks.indexOfFirst { it.parkID == parkID }
        if (index != -1) {
            allParks[index] = allParks[index].copy(isFavorite = newState)
        }
    }
}