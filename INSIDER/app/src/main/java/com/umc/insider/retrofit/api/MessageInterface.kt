package com.umc.insider.retrofit.api

import com.umc.insider.retrofit.model.MessageGetRes
import com.umc.insider.retrofit.model.MessagePostReq
import com.umc.insider.retrofit.model.MessagePostRes
import com.umc.insider.retrofit.response.BaseResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface MessageInterface {

    @POST("/messages/send")
    suspend fun createMessage(@Body messagePostReq : MessagePostReq) : Response<BaseResponse<MessagePostRes>>

    @GET("/messages/{chatRoomId}")
    suspend fun getMessageesInChatRoom(@Path("chatRoomId") chatRoomId : Long) : Response<List<MessageGetRes>>
}