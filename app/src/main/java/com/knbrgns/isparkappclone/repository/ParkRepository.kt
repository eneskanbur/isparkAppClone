package com.knbrgns.isparkappclone.repository

import com.knbrgns.isparkappclone.model.Park
import com.knbrgns.isparkappclone.service.Client

class ParkRepository {
    private val parkAPI = Client.getParkService()

    suspend fun getParks(): Result<List<Park>> {
        return try {
            val response = parkAPI.getParks()

            if (response.isSuccessful) {
                Result.success(response.body() ?: emptyList())
            } else {
                Result.failure(Exception("Park verileri alınamadı"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getParkWithId(parkId: Int): Result<Park> {
        return try {
            val response = parkAPI.getParkWithId(parkId)
            if (response.isSuccessful) {
                response.body()?.let {
                    Result.success(it)
                } ?: Result.failure(Exception("Park bulunamadı"))
            } else {
                Result.failure(Exception("Park detayı alınamadı"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
