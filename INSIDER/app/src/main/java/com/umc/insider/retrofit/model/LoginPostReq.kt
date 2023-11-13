package com.umc.insider.retrofit.model

import com.google.gson.annotations.SerializedName

data class LoginPostReq(

    // 아마도 userId
    @SerializedName("userId")
    val userId : String,

    @SerializedName("pw")
    val pw : String
)
