package com.example.mumuk.utils

import android.util.Base64
import android.util.Log
import org.json.JSONObject

object JwtUtils {
    fun getUserIdFromToken(token: String): Long? {
        return try {
            val parts = token.split(".")
            if (parts.size != 3) {
                Log.e("JwtUtils", "JWT 형식이 아님: $token")
                return null
            }

            val payload = String(Base64.decode(parts[1], Base64.URL_SAFE))
            Log.d("JwtUtils", "payload: $payload")

            val json = JSONObject(payload)

            if (json.has("user_id")) {
                return json.getLong("user_id")
            } else {
                Log.e("JwtUtils", "user_id 키가 존재하지 않음")
                null
            }

        } catch (e: Exception) {
            Log.e("JwtUtils", "user_id 추출 실패", e)
            null
        }
    }
}
