package com.umc.insider.retrofit.response

import com.google.gson.annotations.SerializedName

data class BaseResponse<T>(
    @SerializedName("isSuccess")
    val isSuccess: Boolean,

    @SerializedName("message")
    val message: String,

    @SerializedName("code")
    val code: Int,

    @SerializedName("result")
    val result: T?
)
