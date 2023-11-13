package com.umc.insider.retrofit.model

import com.google.gson.annotations.SerializedName
import java.util.Date

data class ChatRoomInfoGetRes(

    @SerializedName("sellerId")
    val sellerId : Long,

    @SerializedName("buyerId")
    val buyerId : Long,

    @SerializedName("status")
    val status : Int,

    @SerializedName("goodsOrExchangesId")
    val goodsOrExchangesId : Long,

    @SerializedName("sellerOrNot")
    val sellerOrNot : Boolean,

    @SerializedName("buyerOrNot")
    val buyerOrNot : Boolean,

    @SerializedName("createdAt")
    val createdAt : Date,
)
