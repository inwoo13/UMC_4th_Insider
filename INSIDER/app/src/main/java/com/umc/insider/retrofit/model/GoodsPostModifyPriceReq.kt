package com.umc.insider.retrofit.model

import com.google.gson.annotations.SerializedName

data class GoodsPostModifyPriceReq(
    @SerializedName("id")
    val id: Long,

    @SerializedName("price")
    val price: String

)
