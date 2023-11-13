package com.umc.insider.retrofit.model

import com.google.gson.annotations.SerializedName
import com.umc.insider.model.Markets
import com.umc.insider.model.Users

data class UserGetByIdRes (

    @SerializedName("nickname")
    val nickname : String,

    @SerializedName("userId")
    val userId : String,

    @SerializedName("pw")
    val pw : String,

    @SerializedName("email")
    val email : String,

    @SerializedName("zipCode")
    val zipCode : Int,

    @SerializedName("detailAddress")
    val detailAddress : String,

    @SerializedName("img")
    val img : String,

    @SerializedName("sellerOrBuyer")
    val sellerOrBuyer : Int,

    @SerializedName("registerNum")
    val registerNum : Long

)
