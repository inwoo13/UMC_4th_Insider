package com.umc.insider.model

import com.google.gson.annotations.SerializedName
import java.util.Date

data class ChatRooms(

    @SerializedName("Long")
    val id: Long,

    @SerializedName("created_at")
    val created_at: Date,

    @SerializedName("seller")
    val seller: Users,

    @SerializedName("buyer")
    val buyer: Users,

    @SerializedName("goods")
    val goods: Goods,

    @SerializedName("exchanges")
    val exchanges: Exchanges,

    @SerializedName("seller_or_not")
    val seller_or_not: Boolean,

    @SerializedName("buyer_or_not")
    val buyer_or_not: Boolean,

)
