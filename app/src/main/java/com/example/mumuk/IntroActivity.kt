package com.example.mumuk

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.mumuk.data.api.RetrofitClient
import com.example.mumuk.data.api.TokenManager
import com.example.mumuk.ui.MainActivity
import com.example.mumuk.ui.login.LoginIntroActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class IntroActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_intro)

        val prefs = getSharedPreferences("auth", MODE_PRIVATE)
        val refreshToken = prefs.getString("refreshToken", null)
        val loginType = prefs.getString("loginType", "LOCAL") ?: "LOCAL"

        if (refreshToken != null) {
            val loginType = TokenManager.getLoginType(this) ?: "LOCAL"
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val response = RetrofitClient.getAuthApi(this@IntroActivity)
                        .reissueToken(refreshToken, loginType)

                    withContext(Dispatchers.Main) {
                        if (response.isSuccessful && response.body()?.data != null) {
                            val tokenData = response.body()!!.data!!
                            TokenManager.saveTokens(
                                this@IntroActivity,
                                tokenData.accessToken,
                                tokenData.refreshToken
                            )
                            startActivity(Intent(this@IntroActivity, MainActivity::class.java))
                            finish()
                        } else {
                            TokenManager.clearTokens(this@IntroActivity)
                            fallbackToLogin("세션이 만료되었습니다. 다시 로그인 해주세요.")
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        TokenManager.clearTokens(this@IntroActivity)
                        fallbackToLogin("자동 로그인 실패: 네트워크 오류")
                    }
                }
            }
        } else {
            // 자동 로그인이 아닐 경우, 3초 후 로그인 화면으로 이동
            Handler(Looper.getMainLooper()).postDelayed({
                startActivity(Intent(this, LoginIntroActivity::class.java))
                finish()
            }, 3000)
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

//        handleKakaoRedirect(intent)
//        handleNaverRedirect(intent)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
//        handleKakaoRedirect(intent)
//        handleNaverRedirect(intent)
    }

    // 카카오 로그인 처리
//    private fun handleKakaoRedirect(intent: Intent?) {
//        val uri: Uri? = intent?.data
//        if (uri != null && uri.toString().startsWith("kakao7950bf906fc9e8123a3832cb5378ae1b://oauth")) {
//            val code = uri.getQueryParameter("code")
//            if (!code.isNullOrEmpty()) {
//                Log.d("KakaoAuth", "인가코드 수신 완료: $code")
//                sendKakaoCodeToBackend(code)
//            }
//        }
//    }

//    private fun sendKakaoCodeToBackend(code: String) {
//        CoroutineScope(Dispatchers.IO).launch {
//            try {
//                val response = RetrofitClient.getAuthApi(this@IntroActivity).kakaoLogin(code)
//                if (response.isSuccessful) {
//                    val user = response.body()?.data
//                    withContext(Dispatchers.Main) {
//                        if (user != null) {
//                            val prefs = getSharedPreferences("auth", MODE_PRIVATE)
//                            prefs.edit().apply {
//                                putString("refreshToken", user.refreshToken)
//                                putString("email", user.email)
//                                putString("nickName", user.nickName)
//                                putString("profileImage", user.profileImage)
//                                apply()
//                            }
//                            startActivity(Intent(this@IntroActivity, MainActivity::class.java))
//                            finish()
//                        } else {
//                            fallbackToLogin("카카오 로그인 실패: 사용자 정보 없음")
//                        }
//                    }
//                } else {
//                    val msg = response.errorBody()?.string()
//                    Log.e("KakaoAuth", "로그인 실패: $msg")
//                    withContext(Dispatchers.Main) {
//                        fallbackToLogin("카카오 로그인 실패\n${response.code()}: $msg")
//                    }
//                }
//            } catch (e: Exception) {
//                Log.e("KakaoAuth", "예외 발생: ${e.message}")
//                withContext(Dispatchers.Main) {
//                    fallbackToLogin("카카오 로그인 예외: ${e.message}")
//                }
//            }
//        }
//    }

    // 네이버 로그인 처리 추가
//    private fun handleNaverRedirect(intent: Intent?) {
//        val uri = intent?.data
//        if (uri?.scheme == "mumuk" &&
//            uri.host == "login" &&
//            uri.path == "/oauth2/code/naver") {
//
//            val code = uri.getQueryParameter("code")
//            val state = uri.getQueryParameter("state")
//            Log.d("NaverLogin", "IntroActivity: 네이버 redirect 감지됨 → code=$code, state=$state")
//
//            if (!code.isNullOrEmpty()) {
//                sendNaverCodeToBackend(code, state ?: "")
//            }
//        } else {
//            // 네이버가 아닐 경우에도 로그인 화면으로 진입
//            Handler(Looper.getMainLooper()).postDelayed({
//                startActivity(Intent(this, LoginIntroActivity::class.java))
//                finish()
//            }, 3000)
//        }
//    }

//    private fun sendNaverCodeToBackend(code: String, state: String) {
//        CoroutineScope(Dispatchers.IO).launch {
//            try {
//                val response = RetrofitClient.getAuthApi(this@IntroActivity).naverLogin(code, state)
//                if (response.isSuccessful) {
//                    val user = response.body()?.data
//                    withContext(Dispatchers.Main) {
//                        if (user != null) {
//                            TokenManager.saveLoginType(this@IntroActivity, "NAVER")
//                            TokenManager.saveTokens(this@IntroActivity, "", user.refreshToken)
//                            TokenManager.saveUserInfo(this@IntroActivity, user.email, user.nickName, user.profileImage)
//
//                            startActivity(Intent(this@IntroActivity, MainActivity::class.java))
//                            finish()
//                        } else {
//                            fallbackToLogin("네이버 로그인 실패: 사용자 정보 없음")
//                        }
//                    }
//                } else {
//                    val msg = response.errorBody()?.string()
//                    Log.e("NaverLogin", "로그인 실패: $msg")
//                    withContext(Dispatchers.Main) {
//                        fallbackToLogin("네이버 로그인 실패\n${response.code()}: $msg")
//                    }
//                }
//            } catch (e: Exception) {
//                Log.e("NaverLogin", "예외 발생: ${e.message}")
//                withContext(Dispatchers.Main) {
//                    fallbackToLogin("네이버 로그인 예외: ${e.message}")
//                }
//            }
//        }
//    }

    private fun fallbackToLogin(message: String) {
        Log.w("Intro", "로그인 실패 fallback → LoginIntroActivity 이동")
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        startActivity(Intent(this, LoginIntroActivity::class.java))
        finish()
    }
}