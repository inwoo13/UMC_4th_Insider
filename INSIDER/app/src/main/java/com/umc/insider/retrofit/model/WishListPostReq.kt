package com.umc.insider.retrofit.model

import com.google.gson.annotations.SerializedName

data class WishListPostReq(

    @SerializedName("userId")
    val userId : Long,
    @SerializedName("goodsOrExchangesId")
    val goodsOrExchangesId : Long,
    // 0 goods, 1 exchanges
    @SerializedName("status")
    val status : Int
)
