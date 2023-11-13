package com.umc.insider.retrofit.model

import Category
import com.google.gson.annotations.SerializedName
import com.umc.insider.model.Markets
import com.umc.insider.model.Users
import java.sql.Timestamp

data class PartialGoods(
    @SerializedName("title")
    val title: String,

    @SerializedName("name")
    val name: String,

    @SerializedName("price")
    val price: String,

    @SerializedName("weight")
    val weight: String?,

    @SerializedName("rest")
    val rest: Int?,

    @SerializedName("shelf_life")
    val shelfLife: String,

    @SerializedName("category")
    val category: Category,

//    @SerializedName("imageUrl")
//    val imageUrl: String?,

    @SerializedName("sale_price")
    val sale_price: Int?,

    @SerializedName("sale_percent")
    val sale_percent : Int?,
)
