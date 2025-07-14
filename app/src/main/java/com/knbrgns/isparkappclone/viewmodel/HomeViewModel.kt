package com.knbrgns.isparkappclone.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.knbrgns.isparkappclone.model.Campaign
import com.knbrgns.isparkappclone.model.News
import com.knbrgns.isparkappclone.service.Client
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val _news = MutableLiveData<List<News>>()
    val news: MutableLiveData<List<News>> = _news

    private val _campaign = MutableLiveData<List<Campaign>>()
    val campaign: MutableLiveData<List<Campaign>> = _campaign

    val service = Client.apiService

    fun getNews() {
        viewModelScope.launch(Dispatchers.IO) {

            val response = service.getNews()
            if (response.isNotEmpty()) {
                _news.postValue(response)
            }

        }
    }

}