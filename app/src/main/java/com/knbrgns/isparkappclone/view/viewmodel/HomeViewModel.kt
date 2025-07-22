package com.knbrgns.isparkappclone.view.viewmodel

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

    private fun createMockNews(): List<News> {
        return listOf(
            News(
                id = 1,
                title = "İSPARK Yeni Otopark Alanları",
                descriptionLong = "İstanbul'da yeni otopark alanları açılıyor. Bu alanlar şehir merkezinde daha kolay park etmenizi sağlayacak.İstanbul'da yeni otopark alanları açılıyor. Bu alanlar şehir merkezinde daha kolay park etmenizi sağlayacak.İstanbul'da yeni otopark alanları açılıyor. Bu alanlar şehir merkezinde daha kolay park etmenizi sağlayacak.İstanbul'da yeni otopark alanları açılıyor. Bu alanlar şehir merkezinde daha kolay park etmenizi sağlayacak.İstanbul'da yeni otopark alanları açılıyor. Bu alanlar şehir merkezinde daha kolay park etmenizi sağlayacak.İstanbul'da yeni otopark alanları açılıyor. Bu alanlar şehir merkezinde daha kolay park etmenizi sağlayacak.",
                descriptionShort = "Yeni otopark alanları açılıyor",
                imageUrl = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSEhj8VZBgvInkfeuvO_WI64LPoN7DLYwGHhQ&s",
                sDate = "15 Ocak 2025",
                isDeleted = false
            ),
            News(
                id = 2,
                title = "Akıllı Park Sistemi Güncellendi",
                descriptionLong = "İSPARK mobil uygulaması yeni özelliklerle güncellendi. Artık daha hızlı otopark bulabilirsiniz.",
                descriptionShort = "Mobil uygulama güncellendi",
                imageUrl = "https://www.kadikoylife.com/wp-content/uploads/ispark-kampanya.jpg",
                sDate = "12 Ocak 2025",
                isDeleted = false
            )
        )
    }

    private fun createMockCampaigns(): List<Campaign> {
        return listOf(
            Campaign(
                id = 1,
                title = "Yeni Kullanıcı Kampanyası",
                descriptionLong = "İlk kullanıcılar için özel indirim kampanyası. İlk 5 park işleminizde %50 indirim fırsatı.",
                descriptionShort = "İlk kullanıcılar için %50 indirim",
                imageUrl = "https://cms.vodafone.com.tr/static/img/content/22-09/27/ispark_kullanimi.jpg",
                sDate = "10 Ocak 2025",
                isDeleted = false
            ),
            Campaign(
                id = 2,
                title = "Hafta Sonu İndirimi",
                descriptionLong = "Hafta sonları tüm İSPARK otoparkları için özel indirim kampanyası başladı.",
                descriptionShort = "Hafta sonları özel indirim",
                imageUrl = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcR9AFaNZXu9TZ4zVGdpddwmqTKqwQRjjxmj_g&s",
                sDate = "8 Ocak 2025",
                isDeleted = false
            )
        )
    }


    fun initialize() {
        _news.value = createMockNews()
        _campaign.value = createMockCampaigns()

        Log.d(TAG, "🚀 INITIALIZE START -> Full app flow beginning")
        _loading.value = true

        viewModelScope.launch {
            try {
                val loginResult = repository.login("sp", "sp")

                if (loginResult.isSuccess) {
                    val newsResult = repository.getNews()
                    if (newsResult.isSuccess) {
                        val newsList = newsResult.getOrNull()
                        if (newsList.isNullOrEmpty()) {
                            _news.value = createMockNews()
                            Log.d(TAG, "📍 MOCK NEWS -> Using mock data")
                        } else {
                            _news.value = newsList
                            Log.d(TAG, "📍 REAL NEWS -> Using API data")
                        }
                    } else {
                        _news.value = createMockNews()
                        Log.d(TAG, "📍 ERROR NEWS -> Using mock data")
                    }

                    val campaignResult = repository.getCampaigns()
                    if (campaignResult.isSuccess) {
                        val campaignList = campaignResult.getOrNull()
                        if (campaignList.isNullOrEmpty()) {
                            _campaign.value = createMockCampaigns()
                            Log.d(TAG, "📍 MOCK CAMPAIGNS -> Using mock data")
                        } else {
                            _campaign.value = campaignList
                            Log.d(TAG, "📍 REAL CAMPAIGNS -> Using API data")
                        }
                    } else {
                        _campaign.value = createMockCampaigns()
                        Log.d(TAG, "📍 ERROR CAMPAIGNS -> Using mock data")
                    }

                } else {
                    _news.value = createMockNews()
                    _campaign.value = createMockCampaigns()
                    Log.d(TAG, "📍 LOGIN FAILED -> Using all mock data")
                }

            } catch (e: Exception) {
                _news.value = createMockNews()
                _campaign.value = createMockCampaigns()
                Log.d(TAG, "📍 EXCEPTION -> Using all mock data")
            } finally {
                _loading.value = false
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