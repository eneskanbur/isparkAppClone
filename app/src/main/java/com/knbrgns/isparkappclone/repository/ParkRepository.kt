package com.knbrgns.isparkappclone.repository

import android.content.Context
import com.knbrgns.isparkappclone.local.FavoriteParkDao
import com.knbrgns.isparkappclone.local.FavoriteParkDatabase
import com.knbrgns.isparkappclone.model.Park
import com.knbrgns.isparkappclone.service.Client
import java.util.concurrent.locks.LockSupport.park

class ParkRepository {
    private val parkAPI = Client.getParkService()
    private lateinit var favoriteDb: FavoriteParkDao

    fun initDatabase(context: Context) {
        favoriteDb = FavoriteParkDatabase.getDatabase(context).favoriteParkDao()
    }

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

    suspend fun getFavoriteIds(): List<Int> {
        return try {
            favoriteDb.getFavoriteParks().map { it.parkID }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun addToFavorites(park: Park) {
        try {
            favoriteDb.addFavoritePArk(park)
        } catch (e: Exception) {
            // Silent fail
        }
    }

    suspend fun removeFromFavorites(parkId: Int) {
        try {

            favoriteDb.deleteFavoriteParkById(parkId)
        } catch (e: Exception) {
            // Silent fail
        }
    }

}

