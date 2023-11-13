package com.umc.insider.model

import Category
import com.google.gson.annotations.SerializedName
import java.sql.Timestamp

data class Goods(
    @SerializedName("id")
    val id: Long,

    @SerializedName("users_id")
    val usersId: Long,

    @SerializedName("markets_id")
    val marketsId: Long,

    @SerializedName("title")
    val title: String,

    @SerializedName("price")
    val price: String,

    @SerializedName("weight")
    val weight: String?,

    @SerializedName("rest")
    val rest: Int,

    @SerializedName("shelf_life")
    val shelfLife: String,

    @SerializedName("sale_price")
    val sale_price: Int?,

    @SerializedName("sale_percent")
    val sale_percent : Int?,

    @SerializedName("imageUrl")
    val imageUrl: String?,

    @SerializedName("created_at")
    val createdAt: String,

    @SerializedName("updated_at")
    val updatedAt: String,

    @SerializedName("name")
    val name: String?,

    @SerializedName("category")
    val category: Long
)
