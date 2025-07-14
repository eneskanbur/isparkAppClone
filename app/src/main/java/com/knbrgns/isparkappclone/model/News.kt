package com.knbrgns.isparkappclone.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import java.util.Date

@Parcelize
data class News(
    val id: Int,
    val title: String,
    @SerializedName("description_Project")
    val descriptionLong: String,
    @SerializedName("description_Back")
    val descriptionShort: String,
    @SerializedName("url")
    val imageUrl: String,
    val sDate: Date,
    val eDate: Date,
    val isDeleted: Boolean
) : Parcelable
