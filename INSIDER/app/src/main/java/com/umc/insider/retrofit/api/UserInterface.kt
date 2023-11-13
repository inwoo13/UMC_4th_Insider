package com.umc.insider.retrofit.api

import com.umc.insider.model.Users
import com.umc.insider.retrofit.model.LatLngGetRes
import com.umc.insider.retrofit.model.LoginPostReq
import com.umc.insider.retrofit.model.LoginPostRes
import com.umc.insider.retrofit.model.SignUpPostReq
import com.umc.insider.retrofit.model.UserGetByIdRes
import com.umc.insider.retrofit.model.UserPatchReq
import com.umc.insider.retrofit.model.UserPostRes
import com.umc.insider.retrofit.model.UserPutProfileReq
import com.umc.insider.retrofit.model.UserPutReq
import com.umc.insider.retrofit.response.BaseResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path

interface UserInterface {

    // 회원가입
    @POST("/create")
    suspend fun createUser(@Body postUserReq: SignUpPostReq): Response<BaseResponse<SignUpPostReq>>

    @POST("/logIn")
    suspend fun logIn(@Body postLoginReq: LoginPostReq): Response<BaseResponse<LoginPostRes>>

    @GET("/user/{id}")
    suspend fun getUserById(@Path("id") id: Long): UserGetByIdRes

    @PUT("/user/modify")
    suspend fun modifyUser(@Body putUserReq: UserPutReq): BaseResponse<UserPostRes>

    @Multipart
    @PUT("/userProfile/register")
    suspend fun registerProfile(@Part("PutUserProfileReq") putUserProfileReq: UserPutProfileReq,
                                @Part image: MultipartBody.Part): BaseResponse<UserPostRes>

    @GET("/address/{zipCode}")
    suspend fun getLatLng(@Path("zipCode") zipCode: String): LatLngGetRes

    @PATCH("/user/transfer")
    suspend fun patchTransfer(@Body patchUserReq: UserPatchReq): Users
}