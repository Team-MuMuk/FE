package com.example.mumuk.ui.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.commit
import com.example.mumuk.R
import com.example.mumuk.data.api.TokenManager
import com.example.mumuk.databinding.ActivityLoginIntroBinding
import com.example.mumuk.ui.MainActivity
import com.example.mumuk.ui.signup.SignupActivity
import com.example.mumuk.data.model.login.openKakaoLoginPage
import com.navercorp.nid.NaverIdLoginSDK
import com.navercorp.nid.oauth.OAuthLoginCallback
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.io.IOException

class LoginIntroActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginIntroBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityLoginIntroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.btnSignup.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
            finish()
        }

        binding.btnLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        binding.btnLoginKakao.setOnClickListener {
            openKakaoLoginPage(this)
        }

        binding.btnLoginSamsung.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        binding.tvFindAccount.setOnClickListener {
            binding.loginIntroLayout.visibility = View.GONE
            binding.loginIntroFragmentContainer.visibility = View.VISIBLE
            supportFragmentManager.commit {
                setReorderingAllowed(true)
                replace(R.id.login_intro_fragment_container, FindAccountFragment())
                addToBackStack(null)
            }
        }

        binding.btnLoginNaver.setOnClickListener {
            NaverIdLoginSDK.authenticate(this, object : OAuthLoginCallback {
                override fun onSuccess() {
                    val accessToken = NaverIdLoginSDK.getAccessToken()
                    Log.d("NaverLogin", "accessToken 수신: $accessToken")
                    if (!accessToken.isNullOrEmpty()) {
                        loginWithNaverToken(accessToken)
                    } else {
                        Log.e("NaverLogin", "accessToken 없음")
                    }
                }

                override fun onFailure(httpStatus: Int, message: String) {
                    Log.e("NaverLogin", "로그인 실패 - HTTP $httpStatus: $message")
                }

                override fun onError(errorCode: Int, message: String) {
                    Log.e("NaverLogin", "로그인 오류 - Code $errorCode: $message")
                }
            })
        }
    }

    private fun loginWithNaverToken(token: String) {
        val apiURL = "https://openapi.naver.com/v1/nid/me"
        val request = Request.Builder()
            .url(apiURL)
            .addHeader("Authorization", "Bearer $token")
            .build()

        val client = OkHttpClient()
        client.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onFailure(call: okhttp3.Call, e: IOException) {
                Log.e("NaverLogin", "사용자 정보 요청 실패: ${e.message}")
            }

            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                val body = response.body?.string()
                Log.d("NaverLogin", "사용자 정보 응답: $body")

                try {
                    val json = JSONObject(body ?: return)
                    val res = json.getJSONObject("response")
                    val nickname = res.optString("nickname")
                    val email = res.optString("email")
                    val name = res.optString("name")

                    TokenManager.saveLoginType(this@LoginIntroActivity, "NAVER")
                    TokenManager.saveUserInfo(this@LoginIntroActivity, email, nickname, "orange")


                    runOnUiThread {
                        Toast.makeText(
                            this@LoginIntroActivity,
                            "${nickname.ifEmpty { name }}님 환영합니다!",
                            Toast.LENGTH_SHORT
                        ).show()

                        Log.d("NaverLogin", "로그인 성공 - nickname: $nickname, email: $email")

                        startActivity(Intent(this@LoginIntroActivity, MainActivity::class.java))
                        finish()
                    }
                } catch (e: Exception) {
                    Log.e("NaverLogin", "JSON 파싱 오류: ${e.message}")
                }
            }
        })
    }
}
