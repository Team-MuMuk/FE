package com.example.mumuk

import android.app.Application
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
    }
}