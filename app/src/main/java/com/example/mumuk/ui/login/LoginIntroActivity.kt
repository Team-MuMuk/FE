package com.example.mumuk.ui.login

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.commit
import com.example.mumuk.R
import com.example.mumuk.data.api.RetrofitClient
import com.example.mumuk.data.api.TokenManager
import com.example.mumuk.data.model.auth.LoginRequest
import com.example.mumuk.data.model.auth.LoginResponse
import com.example.mumuk.databinding.ActivityLoginIntroBinding
import com.example.mumuk.ui.MainActivity
import com.example.mumuk.ui.signup.SignupActivity
import com.example.mumuk.data.model.login.openKakaoLoginPage
import com.navercorp.nid.NaverIdLoginSDK
import com.navercorp.nid.oauth.OAuthLoginCallback
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class LoginIntroActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginIntroBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginIntroBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()

        ViewCompat.setOnApplyWindowInsetsListener(binding.loginIntroLayout) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val activeDrawable = ContextCompat.getDrawable(this, R.drawable.btn_login_active)
        val inactiveDrawable = ContextCompat.getDrawable(this, R.drawable.logintext_border)
        val whiteTextColor = ContextCompat.getColor(this, android.R.color.white)

        var isPasswordVisible = false
        binding.ivTogglePw.setOnClickListener {
            isPasswordVisible = !isPasswordVisible
            if (isPasswordVisible) {
                binding.etPassword.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                binding.ivTogglePw.setImageResource(R.drawable.ic_eyeopened)
            } else {
                binding.etPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                binding.ivTogglePw.setImageResource(R.drawable.ic_eyeclosed)
            }
            binding.etPassword.setSelection(binding.etPassword.text?.length ?: 0)
        }

        binding.btnLogin.setOnClickListener {
            val loginId = binding.etId.text.toString()
            val password = binding.etPassword.text.toString()

            var hasError = false

            if (loginId.isBlank()) {
                binding.etId.background = ContextCompat.getDrawable(this, R.drawable.bg_login_error)
                binding.etId.setText("")
                binding.etId.hint = "Enter your ID."
                binding.etId.setHintTextColor(ContextCompat.getColor(this, android.R.color.holo_red_light))
                hasError = true
            }

            if (password.isBlank()) {
                binding.etPassword.background = ContextCompat.getDrawable(this, R.drawable.bg_login_error)
                binding.etPassword.setText("")
                binding.etPassword.hint = "Enter your password."
                binding.etPassword.setHintTextColor(ContextCompat.getColor(this, android.R.color.holo_red_light))
                hasError = true
            }

            if (hasError) return@setOnClickListener

            val request = LoginRequest(loginId, password)
            val api = RetrofitClient.getAuthApi(this)

            api.login(request).enqueue(object : Callback<LoginResponse> {
                override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                    if (response.isSuccessful) {
                        val loginResponse = response.body()
                        if (loginResponse?.status == "OK" && loginResponse.data != null) {
                            val accessToken = loginResponse.data.accessToken
                            val refreshToken = loginResponse.data.refreshToken

                            TokenManager.saveTokens(this@LoginIntroActivity, accessToken, refreshToken)
                            TokenManager.saveLoginType(this@LoginIntroActivity, "LOCAL")

                            startActivity(Intent(this@LoginIntroActivity, MainActivity::class.java).apply {
                                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            })
                        } else {
                            Toast.makeText(this@LoginIntroActivity, "로그인 실패: ${loginResponse?.message ?: "알 수 없음"}", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this@LoginIntroActivity, "서버 에러: ${response.code()}", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    Toast.makeText(this@LoginIntroActivity, "네트워크 오류: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }


        val watcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val isIdFilled = binding.etId.text?.isNotEmpty() == true
                val isPasswordFilled = binding.etPassword.text?.isNotEmpty() == true

                if (isIdFilled) {
                    binding.etId.background = ContextCompat.getDrawable(this@LoginIntroActivity, R.drawable.logintext_border)
                    binding.etId.setHintTextColor(ContextCompat.getColor(this@LoginIntroActivity, R.color.gray))
                }

                if (isPasswordFilled) {
                    binding.etPassword.background = ContextCompat.getDrawable(this@LoginIntroActivity, R.drawable.logintext_border)
                    binding.etPassword.setHintTextColor(ContextCompat.getColor(this@LoginIntroActivity, R.color.gray))
                }

                if (isIdFilled && isPasswordFilled) {
                    binding.btnLogin.isEnabled = true
                    binding.btnLogin.background = activeDrawable
                    binding.btnLogin.setTextColor(whiteTextColor)
                } else {
                    binding.btnLogin.isEnabled = false
                    binding.btnLogin.background = inactiveDrawable
                }
            }
        }


        binding.etId.addTextChangedListener(watcher)
        binding.etPassword.addTextChangedListener(watcher)
        binding.btnLogin.isEnabled = false

        binding.btnSignup.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
            finish()
        }

        binding.btnLoginKakao.setOnClickListener {
            openKakaoLoginPage(this)
        }

        binding.btnLoginNaver.setOnClickListener {
            NaverIdLoginSDK.authenticate(this, object : OAuthLoginCallback {
                override fun onSuccess() {
                    val accessToken = NaverIdLoginSDK.getAccessToken()
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

        binding.tvFindAccount.setOnClickListener {
            binding.loginIntroLayout.visibility = View.GONE
            binding.loginIntroFragmentContainer.visibility = View.VISIBLE

            if (supportFragmentManager.findFragmentById(R.id.login_intro_fragment_container) == null) {
                supportFragmentManager.commit {
                    setReorderingAllowed(true)
                    replace(R.id.login_intro_fragment_container, FindAccountFragment())
                    addToBackStack(null)
                }
            }
        }


    }

    private fun loginWithNaverToken(token: String) {
        val apiURL = "https://openapi.naver.com/v1/nid/me"
        val request = Request.Builder()
            .url(apiURL)
            .addHeader("Authorization", "Bearer $token")
            .build()

        OkHttpClient().newCall(request).enqueue(object : okhttp3.Callback {
            override fun onFailure(call: okhttp3.Call, e: IOException) {
                Log.e("NaverLogin", "사용자 정보 요청 실패: ${e.message}")
            }

            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                val body = response.body?.string()
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
