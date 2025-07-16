package com.knbrgns.isparkappclone.repository

import android.content.Context
import android.util.Log
import com.knbrgns.isparkappclone.model.Campaign
import com.knbrgns.isparkappclone.model.LoginRequest
import com.knbrgns.isparkappclone.model.News
import com.knbrgns.isparkappclone.service.Client
import com.knbrgns.isparkappclone.util.TokenManager

class AuthRepository(private val context: Context) {

    companion object {
        private const val TAG = "ISPARK_FLOW"
    }

    private val authService = Client.getAuthService()
    private val dataService = Client.getDataService(context)
    private val tokenManager = TokenManager(context)

    suspend fun login(username: String, password: String): Result<String> {
        Log.d(TAG, "🔑 LOGIN START -> user: $username")

        return try {
            val response = authService.login(LoginRequest(username, password))

            if (response.isSuccessful) {
                val token = response.body()?.token
                if (!token.isNullOrEmpty()) {
                    tokenManager.saveToken(token)
                    Log.d(TAG, "✅ LOGIN SUCCESS -> token saved")
                    Result.success(token)
                } else {
                    Log.e(TAG, "❌ LOGIN FAILED -> empty token")
                    Result.failure(Exception("Token alınamadı"))
                }
            } else {
                Log.e(TAG, "❌ LOGIN FAILED -> ${response.code()}: ${response.message()}")
                Result.failure(Exception("Login başarısız: ${response.message()}"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "❌ LOGIN ERROR -> ${e.message}")
            Result.failure(Exception("Network hatası: ${e.message}"))
        }
    }

    suspend fun getNews(): Result<List<News>> {
        Log.d(TAG, "📰 NEWS REQUEST START")

        return try {
            val response = dataService.getNews()

            if (response.isSuccessful) {
                val newsList = response.body() ?: emptyList()
                Log.d(TAG, "✅ NEWS SUCCESS -> ${newsList.size} items received")
                Result.success(newsList)
            } else {
                Log.e(TAG, "❌ NEWS FAILED -> ${response.code()}: ${response.message()}")
                Result.failure(Exception("News verisi alınamadı: ${response.message()}"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "❌ NEWS ERROR -> ${e.message}")
            Result.failure(Exception("Network hatası: ${e.message}"))
        }
    }

    suspend fun getCampaigns(): Result<List<Campaign>> {
        Log.d(TAG, "🎯 CAMPAIGNS REQUEST START")

        return try {
            val response = dataService.getCampaigns()

            if (response.isSuccessful) {
                val campaignsList = response.body() ?: emptyList()
                Log.d(TAG, "✅ CAMPAIGNS SUCCESS -> ${campaignsList.size} items received")
                Result.success(campaignsList)
            } else {
                Log.e(TAG, "❌ CAMPAIGNS FAILED -> ${response.code()}: ${response.message()}")
                Result.failure(Exception("Campaign verisi alınamadı: ${response.message()}"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "❌ CAMPAIGNS ERROR -> ${e.message}")
            Result.failure(Exception("Network hatası: ${e.message}"))
        }
    }
}