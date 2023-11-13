package com.umc.insider.retrofit.model

import com.google.gson.annotations.SerializedName

data class WishListGetRes(

    @SerializedName("goodsId")
    val goodsId : Long ,

    @SerializedName("title")
    val title : String,

    @SerializedName("price")
    val price : String,

    @SerializedName("weight")
    val weight : String,

    @SerializedName("rest")
    val rest : Int,

    @SerializedName("imageUrl")
    val imageUrl : String,

    @SerializedName("name")
    val name : String
)
