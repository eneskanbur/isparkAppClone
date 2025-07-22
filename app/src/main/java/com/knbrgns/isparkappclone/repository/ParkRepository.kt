package com.knbrgns.isparkappclone.repository

import android.content.Context
import com.knbrgns.isparkappclone.local.FavoriteParkDao
import com.knbrgns.isparkappclone.local.FavoriteParkDatabase
import com.knbrgns.isparkappclone.model.Park
import com.knbrgns.isparkappclone.service.Client

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

    suspend fun addToFavorites(park: Park): Result<Unit> {
        return try {
            favoriteDb.addFavoritePark(park)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun removeFromFavorites(park: Park): Result<Unit> {
        return try {
            favoriteDb.deleteFavoritePark(park)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getFavoriteIds(): Result<List<Park>> {
        return try {
            val favoriteParks = favoriteDb.getFavoriteParks()
            Result.success(favoriteParks)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

}
