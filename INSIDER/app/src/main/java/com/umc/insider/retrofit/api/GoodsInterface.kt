package com.umc.insider.retrofit.api

import com.umc.insider.model.Goods
import com.umc.insider.retrofit.model.GoodsGetRes
import com.umc.insider.retrofit.model.GoodsPostModifyPriceReq
import com.umc.insider.retrofit.model.GoodsPostRes
import com.umc.insider.retrofit.model.PartialGoods
import com.umc.insider.retrofit.response.BaseResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query
import java.io.File

interface GoodsInterface {

    @GET("/goods/read")
    suspend fun getGoods(@Query("title") title: String?): BaseResponse<List<GoodsGetRes>>

    @Multipart
    @POST("/goods/create")
    suspend fun createGoods(
        @Part("postgoodsReq") postgoodsReq: RequestBody,
        @Part image: MultipartBody.Part
    ): Response<BaseResponse<GoodsPostRes>>

    @POST("/goods/modifyPrice")
    suspend fun modifyPrice(
        @Body postModifyPriceReq: GoodsPostModifyPriceReq
    ): Response<BaseResponse<String>>

    @GET("/goods/{id}")
    suspend fun getGoodsById(@Path("id") id: Long): GoodsGetRes

    @GET("/goods/category/{category_id}")
    suspend fun getGoodsByCategoryId(@Path("category_id") category_id : Long) : Response<List<GoodsGetRes>>

    @PUT("/goods/update/{id}")
    suspend fun update(
        @Path("id") id: Long,
        @Body goods: PartialGoods
    ): Response<Goods>

    @DELETE("/goods/delete/{id}")
    suspend fun deleteGoods(@Path("id") id: Long): Long

    @GET("/goods/sale")
    suspend fun getGoodsWithSalePrice() : BaseResponse<List<GoodsGetRes>>

}
