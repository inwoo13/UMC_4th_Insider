package com.umc.insider.retrofit.model

import Category
import com.google.gson.annotations.SerializedName
import com.umc.insider.model.Markets
import com.umc.insider.model.Users

data class GoodsGetRes (

    @SerializedName("id")
    val goods_id : Long,

    @SerializedName("users_id")
    val users_id : Users,

    @SerializedName("category_id")
    val categoryId : Category,

    @SerializedName("markets_id")
    val markets_id : Markets,

    @SerializedName("title")
    val title : String,

    @SerializedName("price")
    val price : String,

    @SerializedName("weight")
    val weight : String?,

    @SerializedName("rest")
    val rest : Int,

    @SerializedName("shelf_life")
    val shelf_life : String,

    @SerializedName("sale_price")
    val sale_price: Int?,

    @SerializedName("sale_percent")
    val sale_percent : Int?,

    @SerializedName("img_url")
    val img_url : String,

    @SerializedName("name")
    val name : String,

    @SerializedName("userZipCode")
    val userZipCode: Int,

    @SerializedName("detailAddress")
    val detailAddress: String,

    @SerializedName("createdAt")
    val createdAt: String

)
