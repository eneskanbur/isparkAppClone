package com.knbrgns.isparkappclone.view.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.knbrgns.isparkappclone.model.Park
import com.knbrgns.isparkappclone.repository.ParkRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ParkDetailViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = ParkRepository()

    private val _parkDetail = MutableLiveData<Park>()
    val parkDetail: MutableLiveData<Park> = _parkDetail

    private val _loading = MutableLiveData<Boolean>(false)
    val loading: MutableLiveData<Boolean> = _loading

    init {
        repository.initDatabase(application)
    }

    fun getParkDetail(parkId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            _loading.postValue(true)
            val result = repository.getParkWithId(parkId)
            if (result.isSuccess) {
                _parkDetail.postValue(result.getOrNull())
            }
            _loading.postValue(false)
        }
    }
}