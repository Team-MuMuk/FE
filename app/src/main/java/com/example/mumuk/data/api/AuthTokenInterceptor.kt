package com.example.mumuk.data.api

import android.content.Context
import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val context: Context) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val urlPath = originalRequest.url.encodedPath

        // 인증이 필요 없는 경로 목록 (나중에 소셜 로그인, 회원가입 등등 넣으시면 됩니다!)
        val noAuthPaths = listOf(
            "/api/auth/login"
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