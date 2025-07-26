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

        if (refreshToken != null) {
            Log.d("AutoLogin", "저장된 refreshToken 발견 → 자동 로그인")

            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
            return
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        handleKakaoRedirect(intent)
    }


    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleKakaoRedirect(intent)
    }


    private fun handleKakaoRedirect(intent: Intent?) {
        val uri: Uri? = intent?.data
        if (uri != null && uri.toString().startsWith("kakao7950bf906fc9e8123a3832cb5378ae1b://oauth")) {
            val code = uri.getQueryParameter("code")
            if (!code.isNullOrEmpty()) {
                Log.d("KakaoAuth", "인가코드 수신 완료: $code")
                sendCodeToBackend(code)
                return
            }
        }

        // 인가코드가 없으면 → 3초 후 로그인 화면으로 이동
        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, LoginIntroActivity::class.java)
            startActivity(intent)
            finish()
        }, 3000)
    }


    private fun sendCodeToBackend(code: String) {
        Log.d("KakaoAuth", "백엔드로 인가코드 전송: $code")

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.getAuthApi(this@IntroActivity).kakaoLogin(code)

                if (response.isSuccessful) {
                    val body = response.body()
                    Log.d("KakaoAuth", "로그인 성공: ${body?.data?.nickName}")

                    withContext(Dispatchers.Main) {
                        body?.data?.let { user ->
                            val prefs = getSharedPreferences("auth", MODE_PRIVATE)
                            prefs.edit().apply {
                                putString("refreshToken", user.refreshToken)
                                putString("email", user.email)
                                putString("nickName", user.nickName)
                                putString("profileImage", user.profileImage)
                                apply()
                            }

                            startActivity(Intent(this@IntroActivity, MainActivity::class.java))
                            finish()
                        } ?: run {
                            fallbackToLogin("로그인 실패: 사용자 정보 없음")
                        }
                    }
                } else {
                    val errorMsg = response.errorBody()?.string()
                    Log.e("KakaoAuth", "로그인 실패: $errorMsg")

                    withContext(Dispatchers.Main) {
                        fallbackToLogin("로그인 실패\n${
                            response.code()
                        }: ${errorMsg ?: "서버 오류"}")
                    }
                }
            } catch (e: Exception) {
                Log.e("KakaoAuth", "예외 발생: ${e.message}")
                withContext(Dispatchers.Main) {
                    fallbackToLogin("예외 발생: ${e.message}")
                }
            }
        }
    }
    private fun fallbackToLogin(message: String) {
        Log.w("KakaoAuth", "로그인 실패 fallback → LoginIntroActivity 이동")
        val intent = Intent(this@IntroActivity, LoginIntroActivity::class.java)
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        startActivity(intent)
        finish()
    }




}
