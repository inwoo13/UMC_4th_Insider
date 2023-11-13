package com.umc.insider.retrofit.model

import com.google.gson.annotations.SerializedName
import java.util.Date

data class ChatRoomsListRes (

    @SerializedName("chatRoomId")
    val chatRoomId : Long,

    @SerializedName("otherNickName")
    val otherNickName : String,

    @SerializedName("lastMessage")
    val lastMessage : String,

    @SerializedName("createdAt")
    val createdAt : Date,

    @SerializedName("goodsId")
    val goodsId : Long,

    @SerializedName("otherImgUrl")
    val otherImgUrl : String
)