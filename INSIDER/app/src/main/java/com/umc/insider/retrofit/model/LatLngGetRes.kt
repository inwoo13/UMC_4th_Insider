package com.umc.insider.retrofit.model

import com.google.gson.annotations.SerializedName

data class LatLngGetRes(
    @SerializedName("latitude")
    val latitude: Double,

    @SerializedName("longitude")
    val longitude: Double
)
