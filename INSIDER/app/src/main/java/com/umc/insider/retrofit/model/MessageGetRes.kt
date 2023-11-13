package com.umc.insider.retrofit.model

import com.google.gson.annotations.SerializedName
import com.umc.insider.model.Users

data class MessageGetRes(

    @SerializedName("id")
    val id : Long,

    @SerializedName("content")
    val content : String,

    @SerializedName("senderId")
    val senderId : Users

)
