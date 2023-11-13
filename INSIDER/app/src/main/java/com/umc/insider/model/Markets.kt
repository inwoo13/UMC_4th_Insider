package com.umc.insider.model

import com.google.gson.annotations.SerializedName

data class Markets(

    @SerializedName("id")
    val id : Long,

    @SerializedName("areas_id")
    val areas_id : Users,

    @SerializedName("name")
    val name : String
)
