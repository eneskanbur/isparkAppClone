package com.knbrgns.isparkappclone.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "myCars")
data class Car(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val carName: String,
    val plate: String,
    val carType: String,
    val parkingLocation: String?,
    val debt: Double
) : Parcelable
