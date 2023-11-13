package com.umc.insider.retrofit.model

import com.google.gson.annotations.SerializedName

data class UserPatchReq(
    @SerializedName("id")
    val id : Long,

    @SerializedName("registerNum")
    val registerNum : Long?
)
