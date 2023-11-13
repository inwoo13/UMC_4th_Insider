package com.umc.insider.retrofit.model

import com.google.gson.annotations.SerializedName

data class BusinessRes(
    @SerializedName("resultCode") val resultCode: Int,
    @SerializedName("resultMsg") val resultMsg: String,
    @SerializedName("totalCount") val totalCount: Int,
    @SerializedName("items") val items: List<RegisterNumCheckGetRes>
)


data class RegisterNumCheckGetRes(
    @SerializedName("EndDt")
    val EndDt: String,

    @SerializedName("TaxTypeCd")
    val TaxTypeCd: String,

    @SerializedName("bno")
    val bno: String,

    @SerializedName("bstt")
    val bstt: String,

    @SerializedName("bsttcd")
    val bsttcd: String,

    @SerializedName("cno")
    val cno: String,

    @SerializedName("company")
    val company: String,

    @SerializedName("taxtype")
    val taxtype: String
)
