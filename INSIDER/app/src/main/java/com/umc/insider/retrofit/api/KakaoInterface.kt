package com.umc.insider.retrofit.api

import com.umc.insider.retrofit.model.KaKaoLoginPostRes
import com.umc.insider.retrofit.model.KaKaoPostRes
import com.umc.insider.retrofit.model.KakaoPostReq
import com.umc.insider.retrofit.response.BaseResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface KakaoInterface {

    @GET("/oauth2/callback/kakao")
    suspend fun kakaoCallback(@Query("code") code: String) : BaseResponse<KaKaoPostRes>

    @POST("/logIn/kakao")
    suspend fun logIn(@Body kakaoPostReq: KakaoPostReq): BaseResponse<KaKaoLoginPostRes>

}