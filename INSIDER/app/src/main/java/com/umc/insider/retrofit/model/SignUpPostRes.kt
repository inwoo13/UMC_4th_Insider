package com.umc.insider.retrofit.model

import com.google.gson.annotations.SerializedName

data class SignUpPostRes(

    @SerializedName("id")
    val id : Long,

    @SerializedName("nickname")
    val nickname : String
)
