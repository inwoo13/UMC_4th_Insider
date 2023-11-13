package com.umc.insider.retrofit.api

import com.umc.insider.model.ChatRooms
import com.umc.insider.model.Users
import com.umc.insider.retrofit.model.ChatRoomInfoGetRes
import com.umc.insider.retrofit.model.ChatRoomsListRes
import com.umc.insider.retrofit.model.ChatRoomsPostReq
import com.umc.insider.retrofit.model.ChatRoomsPostRes
import com.umc.insider.retrofit.model.ChatRoomsUsersGetRes
import com.umc.insider.retrofit.model.ExchangesPostRes
import com.umc.insider.retrofit.model.GoodsGetRes
import com.umc.insider.retrofit.response.BaseResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ChattingInterface {

    // 채팅방 생성
    @POST("/chatRooms/create")
    suspend fun createChatRoom(@Body chatRoomsPostReq: ChatRoomsPostReq): Response<BaseResponse<ChatRoomsPostRes>>

    // 채팅방 참여 유저들 조회
    @GET("/chatRooms/{chatRoomId}/users")
    suspend fun getUsersInChatRoom(@Path("chatRoomId") chatRoomId: Long): Response<List<Users>>

    // 유저의 채팅방 목록 출력
    @GET("/chatRooms/{id}")
    suspend fun getChatRoomByUser(@Path("id") userId: Long) : Response<List<ChatRoomsListRes>>

    // 구매목록 출력
    @GET("/chatRooms/goods/{id}")
    suspend fun getGoodsByUser(@Path("id") userId : Long) : Response<Map<String, List<GoodsGetRes>>>

    // 교환목록 출력
    @GET("/chatRooms/exchanges/{id}")
    suspend fun getExchangesByUser(@Path("id") userId : Long) : Response<Map<String, List<ExchangesPostRes>>>

    @PUT("/chatRooms/purchase/{chatRoomId}/{id}")
    suspend fun purchase(@Path("chatRoomId") chatRoomId : Long, @Path("id") id : Long) : Response<ChatRooms>

    @GET("/chatRooms/info/{chatId}")
    suspend fun getChatRoomByChatRoomId(@Path("chatId") chatId : Long) : BaseResponse<ChatRoomInfoGetRes>
}