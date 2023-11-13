package com.umc.insider.retrofit.model

import com.google.gson.annotations.SerializedName

data class ChatRoomsUsersGetRes (

    @SerializedName("id")
    val id : Long,

    @SerializedName("nickname")
    val nickname : String

)