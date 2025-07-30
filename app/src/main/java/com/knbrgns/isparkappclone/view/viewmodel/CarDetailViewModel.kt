package com.knbrgns.isparkappclone.view.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.knbrgns.isparkappclone.model.Car
import com.knbrgns.isparkappclone.repository.ParkRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CarDetailViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ParkRepository()

    private val _loading = MutableLiveData<Boolean>(false)
    val loading: MutableLiveData<Boolean> = _loading

    private val _deleteSuccess = MutableLiveData<Boolean>()
    val deleteSuccess: MutableLiveData<Boolean> = _deleteSuccess

    private val _error = MutableLiveData<String>()
    val error: MutableLiveData<String> = _error

    init {
        repository.initDatabase(application)
    }

    fun deleteCar(car: Car) {
        viewModelScope.launch(Dispatchers.IO) {
            _loading.postValue(true)
            try {
                repository.deleteCar(car)
                _deleteSuccess.postValue(true)
            } catch (e: Exception) {
                _error.postValue("Araç silinirken hata oluştu: ${e.message}")
            } finally {
                _loading.postValue(false)
            }
        }
    }
}