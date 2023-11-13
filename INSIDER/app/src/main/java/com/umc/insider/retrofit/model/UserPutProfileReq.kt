package com.umc.insider.retrofit.model

import com.google.gson.annotations.SerializedName
import com.umc.insider.model.Markets
import com.umc.insider.model.Users

data class UserPutProfileReq (

    @SerializedName("id")
    val id : Long?

)
