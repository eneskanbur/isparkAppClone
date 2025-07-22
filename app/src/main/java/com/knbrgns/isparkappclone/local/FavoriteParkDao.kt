package com.knbrgns.isparkappclone.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.knbrgns.isparkappclone.model.Park

@Dao
interface  FavoriteParkDao {

    @Query("SELECT * FROM favoritePark")
    suspend fun getFavoriteParks() : List<Park>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addFavoritePArk(park: Park)

    @Query("DELETE FROM favoritePark WHERE parkID = :parkId")
    suspend fun deleteFavoriteParkById(parkId: Int)
}