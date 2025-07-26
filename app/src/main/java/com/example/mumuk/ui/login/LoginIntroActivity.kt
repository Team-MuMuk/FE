package com.example.mumuk.ui.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.commit
import androidx.lifecycle.lifecycleScope
import com.example.mumuk.R
import com.example.mumuk.data.api.RetrofitClient
import com.example.mumuk.data.model.login.openKakaoLoginPage
import com.example.mumuk.databinding.ActivityLoginIntroBinding
import com.example.mumuk.ui.MainActivity
import com.example.mumuk.ui.signup.SignupActivity
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.user.UserApiClient
import com.navercorp.nid.NaverIdLoginSDK
import com.navercorp.nid.oauth.OAuthLoginCallback
import kotlinx.coroutines.launch

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

        // 네이버 로그인 버튼 클릭 시
        binding.btnLoginNaver.setOnClickListener {
            NaverIdLoginSDK.authenticate(this, object : OAuthLoginCallback {
                override fun onSuccess() {
                    val accessToken = NaverIdLoginSDK.getAccessToken()
                    // TODO: accessToken을 서버로 넘기기 등 처리
                    Log.d("NaverLogin", "네이버 로그인 성공, accessToken: $accessToken")
                }

                override fun onFailure(httpStatus: Int, message: String) {
                    Log.e("NaverLogin", "네이버 로그인 실패: $httpStatus, $message")
                }

                override fun onError(errorCode: Int, message: String) {
                    Log.e("NaverLogin", "네이버 로그인 에러: $errorCode, $message")
                }
            })
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
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)

        val uri = intent.data
        Log.d("NaverLogin", "Intent URI: $uri")

        if (uri?.scheme == "mumuk" &&
            uri.host == "login" &&
            uri.path == "/oauth2/code/naver") {

            val code = uri.getQueryParameter("code")
            val state = uri.getQueryParameter("state")

            Log.d("NaverLogin", "code=$code, state=$state")

            if (!code.isNullOrEmpty()) {
                loginWithNaverCode(code, state ?: "")
            } else {
                Log.e("NaverLogin", "code가 비어있음 - 로그인 시도 안함")
            }
        }
    }

    private fun loginWithNaverCode(code: String, state: String) {
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.getAuthApi(this@LoginIntroActivity).naverLogin(code, state)
                if (response.isSuccessful) {
                    val userData = response.body()?.data

                    Log.d("NaverLogin", "로그인 성공: ${userData?.email}")

                    startActivity(Intent(this@LoginIntroActivity, MainActivity::class.java))
                    finish()
                } else {
                    Log.e("NaverLogin", "로그인 실패: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("NaverLogin", "예외 발생: ${e.message}")
            }
        }
    }
}