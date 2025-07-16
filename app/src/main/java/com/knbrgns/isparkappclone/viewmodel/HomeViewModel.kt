package com.knbrgns.isparkappclone.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.knbrgns.isparkappclone.model.Campaign
import com.knbrgns.isparkappclone.model.News
import com.knbrgns.isparkappclone.repository.AuthRepository
import kotlinx.coroutines.launch

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    companion object {
        private const val TAG = "ISPARK_FLOW"
    }

    private val _news = MutableLiveData<List<News>>()
    val news: MutableLiveData<List<News>> = _news

    private val _campaign = MutableLiveData<List<Campaign>>()
    val campaign: MutableLiveData<List<Campaign>> = _campaign

    private val _loading = MutableLiveData<Boolean>()
    val loading: MutableLiveData<Boolean> = _loading

    private val _error = MutableLiveData<String>()
    val error: MutableLiveData<String> = _error

    private val repository = AuthRepository(application)

    fun initialize() {
        Log.d(TAG, "🚀 INITIALIZE START -> Full app flow beginning")
        _loading.value = true

        viewModelScope.launch {
            try {
                // 1. Login işlemi
                Log.d(TAG, "📍 STEP 1 -> Login attempt")
                val loginResult = repository.login("sp", "sp")

                if (loginResult.isSuccess) {
                    Log.d(TAG, "📍 STEP 2 -> Login success, fetching data")

                    // 2. News verilerini getir
                    val newsResult = repository.getNews()
                    if (newsResult.isSuccess) {
                        _news.value = newsResult.getOrNull()
                        Log.d(TAG, "📍 STEP 3 -> News data set to UI")
                    }

                    // 3. Campaign verilerini getir
                    val campaignResult = repository.getCampaigns()
                    if (campaignResult.isSuccess) {
                        _campaign.value = campaignResult.getOrNull()
                        Log.d(TAG, "📍 STEP 4 -> Campaigns data set to UI")
                    }

                    Log.d(TAG, "🎉 INITIALIZE COMPLETE -> All data loaded successfully")

                } else {
                    val errorMessage = loginResult.exceptionOrNull()?.message ?: "Login başarısız"
                    _error.value = errorMessage
                    Log.e(TAG, "❌ INITIALIZE FAILED -> $errorMessage")
                }

            } catch (e: Exception) {
                val errorMessage = "Beklenmeyen hata: ${e.message}"
                _error.value = errorMessage
                Log.e(TAG, "❌ INITIALIZE ERROR -> $errorMessage")
            } finally {
                _loading.value = false
                Log.d(TAG, "🏁 INITIALIZE END -> Loading completed")
            }
        }
    }

    fun getNews() {
        Log.d(TAG, "🔄 MANUAL NEWS -> User requested news refresh")
        viewModelScope.launch {
            _loading.value = true
            try {
                val result = repository.getNews()
                if (result.isSuccess) {
                    _news.value = result.getOrNull()
                    Log.d(TAG, "✅ MANUAL NEWS SUCCESS")
                } else {
                    _error.value = result.exceptionOrNull()?.message
                    Log.e(TAG, "❌ MANUAL NEWS FAILED")
                }
            } catch (e: Exception) {
                _error.value = "News getirilemedi: ${e.message}"
                Log.e(TAG, "❌ MANUAL NEWS ERROR -> ${e.message}")
            } finally {
                _loading.value = false
            }
        }
    }

    fun getCampaigns() {
        Log.d(TAG, "🔄 MANUAL CAMPAIGNS -> User requested campaigns refresh")
        viewModelScope.launch {
            _loading.value = true
            try {
                val result = repository.getCampaigns()
                if (result.isSuccess) {
                    _campaign.value = result.getOrNull()
                    Log.d(TAG, "✅ MANUAL CAMPAIGNS SUCCESS")
                } else {
                    _error.value = result.exceptionOrNull()?.message
                    Log.e(TAG, "❌ MANUAL CAMPAIGNS FAILED")
                }
            } catch (e: Exception) {
                _error.value = "Campaigns getirilemedi: ${e.message}"
                Log.e(TAG, "❌ MANUAL CAMPAIGNS ERROR -> ${e.message}")
            } finally {
                _loading.value = false
            }
        }
    }
}