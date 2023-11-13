package com.umc.insider.retrofit.model

import com.google.gson.annotations.SerializedName
import com.umc.insider.model.Markets
import com.umc.insider.model.Users

data class UserPostRes (

    @SerializedName("id")
    val id : Long,

    @SerializedName("nickname")
    val nickname : String

)
