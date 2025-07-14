package com.knbrgns.isparkappclone.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Campaign(
    val id: Int,
    val title: String,
    @SerializedName("description_Project")
    val descriptionLong: String,
    @SerializedName("description_Back")
    val descriptionShort: String,
    @SerializedName("url")
    val imageUrl: String,
    val sDate: String,
    val isDeleted: Boolean
) : Parcelable
