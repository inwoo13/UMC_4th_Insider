package com.umc.insider.model

import Category
import com.google.gson.annotations.SerializedName
import java.util.Date

data class Exchanges(


    @SerializedName("id")
    val id : Long,

    @SerializedName("category")
    val category: Category,

    @SerializedName("user")
    val user: Users,

    @SerializedName("title")
    val title: String,

    @SerializedName("imageUrl")
    val imageUrl: String,

    @SerializedName("name")
    val name: String,

    @SerializedName("count")
    val count: Int,

    @SerializedName("wantItem")
    val wantItem: String,

    @SerializedName("weight")
    val weight: String,

    @SerializedName("shelfLife")
    val shelfLife: String,

    @SerializedName("created_at")
    val created_at: Date,
)