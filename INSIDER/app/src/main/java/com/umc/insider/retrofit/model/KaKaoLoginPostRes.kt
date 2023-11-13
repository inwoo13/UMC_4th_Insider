package com.umc.insider.retrofit.model

import com.google.gson.annotations.SerializedName

data class KaKaoLoginPostRes(
    @SerializedName("id")
    val id: Long,
    @SerializedName("sellerOrBuyer")
    val sellerOrBuyer: Int
)
