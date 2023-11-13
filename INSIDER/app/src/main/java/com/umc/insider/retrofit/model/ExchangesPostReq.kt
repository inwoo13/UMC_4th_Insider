package com.umc.insider.retrofit.model

import com.google.gson.annotations.SerializedName

data class ExchangesPostReq(

    @SerializedName("title")
    val  title : String,

    @SerializedName("name")
    val  name : String,

    @SerializedName("count")
    val  count : Int?,

    @SerializedName("wantItem")
    val  wantItem : String,

    @SerializedName("weight")
    val  weight : String,

    @SerializedName("shelfLife")
    val  shelfLife : String,

    @SerializedName("categoryId")
    val  categoryId : Long,

    @SerializedName("userId")
    val  userId : Long
)
