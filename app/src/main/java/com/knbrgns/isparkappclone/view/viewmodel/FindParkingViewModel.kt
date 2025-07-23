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

    // ✅ MASTER STATE: Bu listeler her zaman güncel tutulacak
    private var allParks: MutableList<Park> = mutableListOf()
    private var currentDisplayedList: MutableList<Park> = mutableListOf()

    init {
        repository.initDatabase(application)
    }

    fun getParks() {
        viewModelScope.launch(Dispatchers.IO) {
            _loading.postValue(true)
            try {
                val result = repository.getParks()
                if (result.isSuccess) {
                    val newParks = result.getOrNull() ?: emptyList()

                    // Master state'i güncelle
                    allParks.clear()
                    allParks.addAll(newParks)

                    // Current displayed list'i güncelle
                    if (_showOnlyFavorites.value == true) {
                        updateDisplayedListForFavorites()
                    } else {
                        updateDisplayedListForAll()
                    }

                    println("DEBUG: Parks loaded - Total: ${allParks.size}, Displayed: ${currentDisplayedList.size}")
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

    // ✅ DISPLAY LIST MANAGEMENT
    private fun updateDisplayedListForAll() {
        currentDisplayedList.clear()
        currentDisplayedList.addAll(allParks)

        // ✅ Main thread'de UI güncellemesi yap
        _parkList.postValue(currentDisplayedList.toList())
        println("DEBUG: Updated display for ALL - Count: ${currentDisplayedList.size}")
    }

    private fun updateDisplayedListForFavorites() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = repository.getFavoriteParks()
                if (result.isSuccess) {
                    val favoriteParks = result.getOrNull() ?: emptyList()
                    val updatedFavorites = favoriteParks.map { it.copy(isFavorite = true) }

                    currentDisplayedList.clear()
                    currentDisplayedList.addAll(updatedFavorites)
                    _parkList.postValue(currentDisplayedList.toList())
                    println("DEBUG: Updated display for FAVORITES - Count: ${currentDisplayedList.size}")
                }
            } catch (e: Exception) {
                println("DEBUG: Error updating favorites display: ${e.message}")
            }
        }
    }

    // ✅ TOGGLE FAVORITE - Optimized for state preservation
    fun toggleFavorite(park: Park, position: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = repository.toggleFavorite(park)
                if (result.isSuccess) {
                    val newFavoriteState = result.getOrNull() ?: false

                    println("DEBUG: Toggle result - Park: ${park.parkID}, Position: $position, New state: $newFavoriteState")

                    // Master state'i güncelle
                    updateMasterState(park.parkID, newFavoriteState)

                    // ✅ STATE PRESERVATION: Listeyi güncelle (blink yok - sadece data değişir)
                    if (_showOnlyFavorites.value == true) {
                        // Favoriler modunda - listeyi yenile
                        updateDisplayedListForFavorites()
                    } else {
                        // Tümü modunda - listeyi yenile
                        updateDisplayedListForAll()
                    }
                }
            } catch (e: Exception) {
                println("DEBUG: Toggle favorite error: ${e.message}")
            }
        }
    }

    private fun updateMasterState(parkID: Int, newFavoriteState: Boolean) {
        val parkIndex = allParks.indexOfFirst { it.parkID == parkID }
        if (parkIndex != -1) {
            allParks[parkIndex] = allParks[parkIndex].copy(isFavorite = newFavoriteState)
            println("DEBUG: Updated master state - Park: $parkID, New state: $newFavoriteState")
        }
    }

    // ✅ TAB SWITCHING
    fun showFavoritesOnly() {
        _loading.postValue(true)
        _showOnlyFavorites.postValue(true)
        updateDisplayedListForFavorites()
        _loading.postValue(false)
    }

    fun showAllParks() {
        _showOnlyFavorites.postValue(false)
        updateDisplayedListForAll()
    }

    // ✅ RESTORE DISPLAY - Navigation'dan dönüldüğünde
    fun restoreDisplay() {
        println("DEBUG: Restoring display - Current mode: ${if (_showOnlyFavorites.value == true) "FAVORITES" else "ALL"}")
        println("DEBUG: Master state - AllParks: ${allParks.size}, CurrentDisplay: ${currentDisplayedList.size}")

        if (currentDisplayedList.isEmpty() && allParks.isNotEmpty()) {
            // Eğer displayed list boşsa ve master list doluysa, restore et
            if (_showOnlyFavorites.value == true) {
                updateDisplayedListForFavorites()
            } else {
                updateDisplayedListForAll()
            }
        } else {
            // Mevcut displayed list'i tekrar emit et
            _parkList.postValue(currentDisplayedList.toList())
            println("DEBUG: Re-emitted current display list - Count: ${currentDisplayedList.size}")
        }
    }

    // ✅ INITIALIZATION
    fun initialize() {
        if (allParks.isEmpty()) {
            println("DEBUG: Initializing - Loading parks")
            getParks()
        } else {
            println("DEBUG: Already initialized - Restoring display")
            restoreDisplay()
        }
    }

    // ✅ GET CURRENT STATE
    fun getCurrentListSize(): Int = currentDisplayedList.size

    fun isShowingFavorites(): Boolean = _showOnlyFavorites.value == true
}