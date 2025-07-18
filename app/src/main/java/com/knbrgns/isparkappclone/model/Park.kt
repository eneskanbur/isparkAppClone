package com.knbrgns.isparkappclone.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Park(
    val parkID: Int,
    @SerializedName(value = "parkName", alternate = ["locationName"])
    val parkName: String,
    val lat: String,
    val lng: String,
    val capacity: Int,
    val emptyCapacity: Int,
    val workHours: String,
    val parkType: String,
    val freeTime: Int,
    val district: String,

    // Diğer nullable alanlar
    val isOpen: Int? = null,
    val updateDate: String? = null,
    val monthlyFee: Int? = null,
    val tariff: String? = null,
    val address: String? = null,
    val areaPolygon: String? = null
) : Parcelable
