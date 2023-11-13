package com.umc.insider.retrofit.model

import com.google.gson.annotations.SerializedName

data class LoginPostRes (

    @SerializedName("id")
    val id : Long,

    @SerializedName("jwt")
    val jwt : String,

    @SerializedName("sellerOrBuyer")
    val sellerOrBuyer : Int
)