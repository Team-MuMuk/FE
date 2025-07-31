package com.example.mumuk

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import android.util.Log

class MyFirebaseMessagingService : FirebaseMessagingService() {
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCM", "FCM Token: $token") // 여기서 토큰 확인
        // 서버에 토큰 전송 코드 추가
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        // 푸시 알림 처리
    }
}