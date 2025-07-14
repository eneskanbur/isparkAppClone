package com.knbrgns.isparkappclone.repository

import android.content.Context
import com.knbrgns.isparkappclone.model.Campaign
import com.knbrgns.isparkappclone.model.LoginRequest
import com.knbrgns.isparkappclone.model.News
import com.knbrgns.isparkappclone.service.Client
import com.knbrgns.isparkappclone.util.TokenManager

class AuthRepository(private val context: Context) {

    private val authService = Client.getAuthService()
    private val dataService = Client.getDataService(context)
    private val tokenManager = TokenManager(context)

    // Login yap ve token'ı kaydet
    suspend fun login(username: String, password: String): Result<String> {
        return try {
            val response = authService.login(LoginRequest(username, password))

            if (response.isSuccessful) {
                val token = response.body()?.token
                if (!token.isNullOrEmpty()) {
                    tokenManager.saveToken(token)
                    Result.success(token)
                } else {
                    Result.failure(Exception("Token alınamadı"))
                }
            } else {
                Result.failure(Exception("Login başarısız: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Network hatası: ${e.message}"))
        }
    }

    // News verilerini getir
    suspend fun getNews(): Result<List<News>> {
        return try {
            val response = dataService.getNews()
            if (response.isSuccessful) {
                Result.success(response.body() ?: emptyList())
            } else {
                Result.failure(Exception("News verisi alınamadı: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Network hatası: ${e.message}"))
        }
    }

    // Campaign verilerini getir
    suspend fun getCampaigns(): Result<List<Campaign>> {
        return try {
            val response = dataService.getCampaigns()
            if (response.isSuccessful) {
                Result.success(response.body() ?: emptyList())
            } else {
                Result.failure(Exception("Campaign verisi alınamadı: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Network hatası: ${e.message}"))
        }
    }
}