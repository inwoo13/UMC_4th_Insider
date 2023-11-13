package com.umc.insider.retrofit.model

import com.google.gson.annotations.SerializedName

data class GoodsPostReq(
    @SerializedName("title")
    val title: String,

    @SerializedName("price")
    val price: String,

    @SerializedName("rest")
    val rest: Int?,

    @SerializedName("shelf_life")
    val shelf_life: String,

    @SerializedName("userIdx")
    val userIdx: Long,

    @SerializedName("name")
    val name : String,

    @SerializedName("categoryId")
    val categoryId : Long,

    @SerializedName("weight")
    val weight : String?

)
