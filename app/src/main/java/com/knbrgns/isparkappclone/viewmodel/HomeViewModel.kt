package com.knbrgns.isparkappclone.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.knbrgns.isparkappclone.model.Campaign
import com.knbrgns.isparkappclone.model.News
import com.knbrgns.isparkappclone.repository.AuthRepository
import kotlinx.coroutines.launch

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val _news = MutableLiveData<List<News>>()
    val news: MutableLiveData<List<News>> = _news

    private val _campaign = MutableLiveData<List<Campaign>>()
    val campaign: MutableLiveData<List<Campaign>> = _campaign

    private val _loading = MutableLiveData<Boolean>()
    val loading: MutableLiveData<Boolean> = _loading

    private val _error = MutableLiveData<String>()
    val error: MutableLiveData<String> = _error

    private val repository = AuthRepository(application)

    // Ana initialization fonksiyonu
    fun initialize() {
        _loading.value = true

        viewModelScope.launch {
            try {
                // 1. Login yap
                val loginResult = repository.login("sp", "sp")

                if (loginResult.isSuccess) {
                    // 2. News verilerini getir
                    val newsResult = repository.getNews()
                    if (newsResult.isSuccess) {
                        _news.value = newsResult.getOrNull()
                    }

                    // 3. Campaign verilerini getir
                    val campaignResult = repository.getCampaigns()
                    if (campaignResult.isSuccess) {
                        _campaign.value = campaignResult.getOrNull()
                    }

                } else {
                    _error.value = loginResult.exceptionOrNull()?.message ?: "Login başarısız"
                }

            } catch (e: Exception) {
                _error.value = "Beklenmeyen hata: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    // Manuel news getirme
    fun getNews() {
        viewModelScope.launch {
            _loading.value = true
            try {
                val result = repository.getNews()
                if (result.isSuccess) {
                    _news.value = result.getOrNull()
                } else {
                    _error.value = result.exceptionOrNull()?.message
                }
            } catch (e: Exception) {
                _error.value = "News getirilemedi: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    // Manuel campaign getirme
    fun getCampaigns() {
        viewModelScope.launch {
            _loading.value = true
            try {
                val result = repository.getCampaigns()
                if (result.isSuccess) {
                    _campaign.value = result.getOrNull()
                } else {
                    _error.value = result.exceptionOrNull()?.message
                }
            } catch (e: Exception) {
                _error.value = "Campaigns getirilemedi: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }
}