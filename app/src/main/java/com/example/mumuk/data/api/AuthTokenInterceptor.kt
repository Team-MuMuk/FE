package com.example.mumuk.data.api

import android.content.Context
import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val context: Context) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val urlPath = originalRequest.url.encodedPath

        val noAuthPaths = listOf(
            "/api/auth/login",
            "/api/auth/kakao-login"
        )

        if (noAuthPaths.any { urlPath.contains(it) }) {
            return chain.proceed(originalRequest)
        }

        val token = TokenManager.getAccessToken(context)

        return if (token != null) {
            val newRequest = originalRequest.newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
            chain.proceed(newRequest)
        } else {
            chain.proceed(originalRequest)
        }
    }
}