package com.example.mumuk.data.api

import android.content.Context
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "https://api.mumuk.site"
    private var retrofit: Retrofit? = null

    private fun getRetrofit(context: Context): Retrofit {
        if (retrofit == null) {
            val client = OkHttpClient.Builder()
                .addInterceptor(AuthInterceptor(context))
                .build()

            retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()
        }
        return retrofit!!
    }

    fun getAuthApi(context: Context): AuthApiService {
        return getRetrofit(context).create(AuthApiService::class.java)
    }

    //TODO: 다른 api도 getAuthApi처럼 get~~Api 와 같이 추가해서 쓰기

    fun getUserApi(context: Context): UserApiService {
        return getRetrofit(context).create(UserApiService::class.java)
    }

}