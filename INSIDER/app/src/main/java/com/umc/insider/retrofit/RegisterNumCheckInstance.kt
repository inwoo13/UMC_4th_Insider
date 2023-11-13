package com.umc.insider.retrofit

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RegisterNumCheckInstance {

    companion object{

        private const val BASE_URL = "https://bizno.net/"

        private var INSTANCE : Retrofit? = null

        fun getInstance() : Retrofit {
            if(INSTANCE == null){
                INSTANCE = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
            }

            return INSTANCE!!
        }
    }
}