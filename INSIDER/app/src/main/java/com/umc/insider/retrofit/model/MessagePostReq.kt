package com.umc.insider.retrofit.model

import com.google.gson.annotations.SerializedName

data class MessagePostReq(

    @SerializedName("chatRoomId")
    val chatRoomId : Long,

    @SerializedName("senderId")
    val senderId : Long,

    @SerializedName("content")
    val content : String
)
