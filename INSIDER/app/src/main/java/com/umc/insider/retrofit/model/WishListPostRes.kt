package com.umc.insider.retrofit.model

import com.google.gson.annotations.SerializedName
import java.util.Date

data class WishListPostRes(

    @SerializedName("wishListId")
    val wishListId : Long,

    @SerializedName("userId")
    val userId : Long,

    @SerializedName("goodsId")
    val goodsId : Long,

    @SerializedName("createdAt")
    val createdAt : Date
)
