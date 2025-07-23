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
                val parks = response.body() ?: emptyList()
                val updatedParks = checkFavorites(parks)
                Result.success(updatedParks)
            } else {
                Result.failure(Exception("Park verileri alınamadı"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    private suspend fun checkFavorites(parks: List<Park>): List<Park> {
        val favoriteParks = favoriteDb.getFavoriteParks()
        val favoriteIds = favoriteParks.map { it.parkID }.toSet()

        return parks.map { park ->
            park.copy(isFavorite = favoriteIds.contains(park.parkID))
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

    suspend fun toggleFavorite(park: Park): Result<Boolean> {
        return try {
            if (park.isFavorite) {
                favoriteDb.deleteFavoritePark(park)
            } else {
                favoriteDb.addFavoritePark(park)
            }
            Result.success(!park.isFavorite)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getFavoriteParks(): Result<List<Park>> {
        return try {
            val favoriteParks = favoriteDb.getFavoriteParks()
            Result.success(favoriteParks)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

}
