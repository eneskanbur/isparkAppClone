package com.knbrgns.isparkappclone.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.knbrgns.isparkappclone.model.Car
import com.knbrgns.isparkappclone.model.Park

@Database(
    entities = [Park::class, Car::class],
    version = 3,
    exportSchema = false
)
abstract class FavoriteParkDatabase : RoomDatabase() {

    abstract fun favoriteParkDao(): FavoriteParkDao

    companion object {
        @Volatile
        private var INSTANCE: FavoriteParkDatabase? = null

        fun getDatabase(context: Context): FavoriteParkDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FavoriteParkDatabase::class.java,
                    "favorite_park_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}