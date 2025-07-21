package com.knbrgns.isparkappclone.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.knbrgns.isparkappclone.model.Park


@Database(entities = [Park::class], version = 1, exportSchema = false)

abstract class FavoriteParkDatabase : RoomDatabase() {

    abstract fun FavoriteParkDao(): FavoriteParkDao

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
                    .fallbackToDestructiveMigration(false) // Sadece development için
                    .build()

                INSTANCE = instance
                instance
            }

        }

    }

}