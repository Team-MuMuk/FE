package com.example.mumuk.data.api

import android.content.Context

object TokenManager {
    private const val PREF_NAME = "auth"
    private const val KEY_ACCESS_TOKEN = "accessToken"
    private const val KEY_REFRESH_TOKEN = "refreshToken"
    private const val KEY_EMAIL = "email"
    private const val KEY_NICKNAME = "nickName"
    private const val KEY_PROFILE_IMAGE = "profileImage"
    private const val KEY_LOGIN_TYPE = "loginType"


    fun saveTokens(context: Context, accessToken: String, refreshToken: String) {
        val pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        pref.edit()
            .putString(KEY_ACCESS_TOKEN, accessToken)
            .putString(KEY_REFRESH_TOKEN, refreshToken)
            .apply()
    }

    fun saveUserInfo(context: Context, email: String?, nickName: String?, profileImage: String?) {
        val pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        pref.edit()
            .putString(KEY_EMAIL, email)
            .putString(KEY_NICKNAME, nickName)
            .putString(KEY_PROFILE_IMAGE, profileImage)
            .apply()
    }

    fun getAccessToken(context: Context): String? {
        val pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return pref.getString(KEY_ACCESS_TOKEN, null)
    }

    fun getRefreshToken(context: Context): String? {
        val pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return pref.getString(KEY_REFRESH_TOKEN, null)
    }

    fun getEmail(context: Context): String? {
        val pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return pref.getString(KEY_EMAIL, null)
    }

    fun getNickName(context: Context): String? {
        val pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return pref.getString(KEY_NICKNAME, null)
    }

    fun getProfileImage(context: Context): String? {
        val pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return pref.getString(KEY_PROFILE_IMAGE, null)
    }

    fun clearTokens(context: Context) {
        val pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        pref.edit()
            .remove(KEY_ACCESS_TOKEN)
            .remove(KEY_REFRESH_TOKEN)
            .remove(KEY_EMAIL)
            .remove(KEY_NICKNAME)
            .remove(KEY_PROFILE_IMAGE)
            .remove(KEY_LOGIN_TYPE)
            .apply()
    }

    fun saveLoginType(context: Context, loginType: String) {
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_LOGIN_TYPE, loginType)
            .apply()
    }

    fun getLoginType(context: Context): String? {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .getString(KEY_LOGIN_TYPE, null)
    }

}
