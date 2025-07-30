package com.knbrgns.isparkappclone.view.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.knbrgns.isparkappclone.model.Car
import com.knbrgns.isparkappclone.repository.ParkRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MyCarViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ParkRepository = ParkRepository()

    private val _cars = MutableLiveData<List<Car>>()
    val cars: MutableLiveData<List<Car>> = _cars

    private val _loading = MutableLiveData<Boolean>()
    val loading: MutableLiveData<Boolean> = _loading

    fun initialize() {
        getCars()
    }

    private fun getCars() {
        viewModelScope.launch(Dispatchers.IO) {
            _loading.postValue(true)

            val result = repository.getCars()
            if (result.isSuccess) {
                _cars.postValue(result.getOrNull())
            } else {
                _cars.postValue(emptyList())
            }
            _loading.postValue(false)
        }
    }

    private fun addCar(car: Car) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addCar(car)
        }
    }

    private fun deleteCar(car: Car) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteCar(car)
        }
    }

}