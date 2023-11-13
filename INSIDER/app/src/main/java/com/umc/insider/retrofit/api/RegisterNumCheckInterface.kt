package com.umc.insider.retrofit.api

import android.provider.Settings.Global.getString
import com.umc.insider.R
import com.umc.insider.retrofit.model.BusinessRes
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface RegisterNumCheckInterface {

    @GET("/api/fapi")
    suspend fun fetchData(
        @Query("key") key: String,
        @Query("gb") gb: Int = 1,
        @Query("status") status : String = "Y",
        @Query("q") query: String,
        @Query("type") type: String = "json"
    ): Response<BusinessRes>
}