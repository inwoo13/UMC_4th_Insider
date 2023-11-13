package com.umc.insider.retrofit.model

import com.google.gson.annotations.SerializedName

data class KakaoPostReq(
    @SerializedName("userId")
    val userId : String
)
