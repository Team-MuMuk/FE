package com.example.mumuk.data.model.login

import android.content.Context
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent

fun openKakaoLoginPage(context: Context) {
    val kakaoAuthUrl = Uri.parse(
        "https://kauth.kakao.com/oauth/authorize" +
                "?response_type=code" +
                "&client_id=7950bf906fc9e8123a3832cb5378ae1b" +
                "&redirect_uri=kakao7950bf906fc9e8123a3832cb5378ae1b://oauth"
    )

    val customTabsIntent = CustomTabsIntent.Builder().build()
    customTabsIntent.launchUrl(context, kakaoAuthUrl)
}
