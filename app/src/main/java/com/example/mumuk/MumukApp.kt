package com.example.mumuk

import android.app.Application
import com.kakao.sdk.common.KakaoSdk
import com.navercorp.nid.NaverIdLoginSDK

class MumukApp : Application() {
    override fun onCreate() {
        super.onCreate()
        NaverIdLoginSDK.initialize(
            context = this,
            clientId = "ELOxdm170OLtWxzA7nlr",
            clientSecret = "agp8iF1gmk",
            clientName = "MUMUK"
        )
        KakaoSdk.init(this, "7950bf906fc9e8123a3832cb5378ae1b")
    }
}