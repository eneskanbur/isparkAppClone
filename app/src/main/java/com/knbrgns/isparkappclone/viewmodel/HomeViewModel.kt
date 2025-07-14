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

    private var authToken: String = "Bearer your_token_here"


    fun getNews() {
        viewModelScope.launch(Dispatchers.IO) {

            try {
                val response = service.getNews(authToken)

                if (response.isSuccessful) {
                    _news.postValue(response.body())
                }
            } catch (e: Exception) {

            }
        }
    }

    fun getNewsWithID(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = service.getNewWithId(authToken, id)
                if (response.isSuccessful && response.body() != null) {
                    _news.postValue(response.body())
                }
            } catch (e: Exception) {
            }
        }

        fun getCampaigns() {
            viewModelScope.launch(Dispatchers.IO) {
                try {
                    val response = service.getCampaigns(authToken)
                    if (response.isSuccessful) {
                        _campaign.postValue(response.body())
                    }
                } catch (e: Exception) {

                }
            }
        }

        fun getCampaignWithId(id: Int) {
            viewModelScope.launch(Dispatchers.IO) {
                try {
                    val response = service.getCampaignById(authToken, id)
                    if (response.isSuccessful && response.body() != null) {
                        _campaign.postValue(response.body())
                    }
                } catch (e: Exception) {

                }

            }

        }
    }
}