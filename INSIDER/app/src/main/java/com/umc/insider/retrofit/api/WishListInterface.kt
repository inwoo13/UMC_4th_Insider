package com.umc.insider.retrofit.api

import com.umc.insider.retrofit.model.ExchangesPostRes
import com.umc.insider.retrofit.model.GoodsGetRes
import com.umc.insider.retrofit.model.WishListGetRes
import com.umc.insider.retrofit.model.WishListPostReq
import com.umc.insider.retrofit.model.WishListPostRes
import com.umc.insider.retrofit.response.BaseResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface WishListInterface {

    @POST("/wishlist/create")
    suspend fun addGoodsToWishList(@Body postWishListsReq: WishListPostReq): Response<BaseResponse<WishListPostRes>>

    @GET("/wishlist/goods/{userId}")
    suspend fun getGoodsInWishList (@Path("userId") userId : Long) : Response<List<GoodsGetRes>>

    @GET("/wishlist/exchanges/{userId}")
    suspend fun getExchangesInWishList(@Path("userId") userId : Long) : Response<List<ExchangesPostRes>>

    @DELETE("/wishlist/delete/{userId}/{goodsOrExchangesId}/{status}")
    suspend fun deleteWishList(
        @Path("userId") userId: Long,
        @Path("goodsOrExchangesId") goodsOrExchangesId: Long,
        @Path("status") status : Int
    ): Response<BaseResponse<WishListPostRes>>

    @GET("/wishlist/check/{userId}/{goodsOrExchangesId}/{status}")
    suspend fun checkWishList(
        @Path("userId") userId : Long,
        @Path("goodsOrExchangesId") goodsOrExchangesId : Long,
        @Path("status") status : Int
    ) : Boolean

    @GET("/wishlist/hot")
    suspend fun getHotGoods() : BaseResponse<List<GoodsGetRes>>
}