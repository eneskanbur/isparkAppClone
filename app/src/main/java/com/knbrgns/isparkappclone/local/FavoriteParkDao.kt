package com.knbrgns.isparkappclone.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.knbrgns.isparkappclone.model.Car
import com.knbrgns.isparkappclone.model.Park

@Dao
interface  FavoriteParkDao {

    @Query("SELECT * FROM favoritePark")
    suspend fun getFavoriteParks() : List<Park>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addFavoritePark(park: Park)

    @Delete
    suspend fun deleteFavoritePark(park: Park)

    @Query("SELECT * FROM myCars")
    suspend fun getCars() : List<Car>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addCar(car: Car)

    @Delete
    suspend fun deleteCar(car: Car)
}