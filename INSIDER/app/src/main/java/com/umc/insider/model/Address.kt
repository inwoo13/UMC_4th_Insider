package com.umc.insider.model

import com.google.gson.annotations.SerializedName

data class Address(

    @SerializedName("id")
    val id: Long,

    @SerializedName("zipCode")
    val zipCode: Int,

    @SerializedName("detailAddress")
    val detailAddress: String

)
