package com.knbrgns.isparkappclone.viewmodel

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

    fun getParks(){
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = repository.getParks()
                if (result.isSuccess){
                    _parkList.postValue(result.getOrNull())
                }
            }catch (e: Exception){

            }

        }
    }

    fun getParkWithId(parkId: Int){
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = repository.getParkWithId(parkId)
                if (result.isSuccess && result.getOrNull() != null){
                    _parkList.postValue(listOf(result.getOrNull() as Park))
                }
            }catch (e: Exception){

            }
        }
    }

    fun initialize() {
        getParks()
    }

}