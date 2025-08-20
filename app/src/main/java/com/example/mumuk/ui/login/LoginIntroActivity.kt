package com.example.mumuk.ui.login

import android.app.Dialog
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.commit
import com.example.mumuk.R
import com.example.mumuk.data.api.RetrofitClient
import com.example.mumuk.data.api.TokenManager
import com.example.mumuk.data.model.auth.KakaoLoginResponse
import com.example.mumuk.data.model.auth.LoginRequest
import com.example.mumuk.data.model.auth.LoginResponse
import com.example.mumuk.data.model.auth.NaverLoginResponse
import com.example.mumuk.databinding.ActivityLoginIntroBinding
import com.example.mumuk.ui.MainActivity
import com.example.mumuk.ui.signup.SignupActivity
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import com.navercorp.nid.NaverIdLoginSDK
import com.navercorp.nid.oauth.OAuthLoginCallback
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.util.UUID // UUID import
import android.view.animation.Animation
import android.view.animation.AnimationUtils

class LoginIntroActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginIntroBinding

    companion object {
        private const val TAG = "LoginIntroActivity"
    }

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
        val defaultDrawable = ContextCompat.getDrawable(this, R.drawable.logintext_border)
        val whiteTextColor = ContextCompat.getColor(this, android.R.color.white)
        val blackTextColor = ContextCompat.getColor(this, android.R.color.black)

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

            Log.d("LoginCheck", "🟡 로그인 시도: ID=[$loginId], PW=[$password]")

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
                    Log.d("LoginCheck", "응답 성공 여부: ${response.isSuccessful}")
                    Log.d("LoginCheck", "응답 코드: ${response.code()}")

                    if (response.isSuccessful) {
                        val loginResponse = response.body()
                        Log.d("LoginCheck", "서버 status: ${loginResponse?.status}")
                        Log.d("LoginCheck", "서버 data: ${loginResponse?.data}")

                        if (loginResponse?.status == "OK" && loginResponse.data != null) {
                            TokenManager.saveTokens(this@LoginIntroActivity, loginResponse.data.accessToken, loginResponse.data.refreshToken)
                            TokenManager.saveLoginType(this@LoginIntroActivity, "LOCAL")

                            Log.d("LoginCheck", "🎉 로그인 성공! MainActivity 이동")

                            startActivity(Intent(this@LoginIntroActivity, MainActivity::class.java).apply {
                                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            })
                            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                            finish() // finish() 추가
                        } else {
                            Log.e("LoginCheck", "로그인 실패 - 서버 응답은 왔지만 status가 OK가 아니거나 data가 없음")
                            showSimpleConfirmDialog(
                                message = "등록되지 않은 아이디거나,\nID 또는 비밀번호를 \n잘못 입력하였습니다."
                            )
                        }
                    } else {
                        val errorBody = response.errorBody()?.string()
                        Log.e("LoginCheck", "응답 실패 - errorBody: $errorBody")
                        showSimpleConfirmDialog(
                            message = "등록되지 않은 아이디거나,\nID 또는 비밀번호를 \n잘못 입력하였습니다."
                        )
                    }
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    Log.e("LoginCheck", "네트워크 오류: ${t.message}")
                    showSimpleConfirmDialog(
                        message = "일시적인 오류로 로그인을 할 수 없습니다.\n잠시 후 다시 시도해 주세요."
                    )
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
                    binding.etId.setBackgroundResource(R.drawable.logintext_border_selector)
                    binding.etId.setHintTextColor(ContextCompat.getColor(this@LoginIntroActivity, R.color.gray))
                }

                if (isPasswordFilled) {
                    binding.etPassword.setBackgroundResource(R.drawable.logintext_border_selector)
                    binding.etPassword.setHintTextColor(ContextCompat.getColor(this@LoginIntroActivity, R.color.gray))
                }

                if (isIdFilled && isPasswordFilled) {
                    binding.btnLogin.background = activeDrawable
                    binding.btnLogin.setTextColor(whiteTextColor)
                } else {
                    binding.btnLogin.background = defaultDrawable
                    binding.btnLogin.setTextColor(blackTextColor)
                }
            }
        }

        binding.etId.addTextChangedListener(watcher)
        binding.etPassword.addTextChangedListener(watcher)
        binding.btnLogin.isEnabled = true

        binding.btnSignup.setOnClickListener {
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)

            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)

        }

        binding.btnLoginKakao.setOnClickListener {
            startKakaoLogin()
        }

        binding.btnLoginNaver.setOnClickListener {
            startNaverLogin()
        }

        binding.tvFindAccount.setOnClickListener {
            binding.loginIntroLayout.visibility = View.GONE
            binding.loginIntroFragmentContainer.visibility = View.VISIBLE

            if (supportFragmentManager.findFragmentById(R.id.login_intro_fragment_container) == null) {
                supportFragmentManager.commit {
                    setReorderingAllowed(true)

                    setCustomAnimations(
                        R.anim.slide_in_right,
                        R.anim.slide_out_left,
                        R.anim.slide_in_left,
                        R.anim.slide_out_right
                    )

                    replace(R.id.login_intro_fragment_container, FindAccountFragment())
                    addToBackStack(null)
                }
            }
        }

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val isFragmentVisible = binding.loginIntroFragmentContainer.visibility == View.VISIBLE
                if (isFragmentVisible && supportFragmentManager.backStackEntryCount > 0) {

                    val slideOut = AnimationUtils.loadAnimation(
                        this@LoginIntroActivity, R.anim.slide_out_right
                    )
                    binding.loginIntroFragmentContainer.startAnimation(slideOut)

                    slideOut.setAnimationListener(object : Animation.AnimationListener {
                        override fun onAnimationStart(animation: Animation?) {}
                        override fun onAnimationRepeat(animation: Animation?) {}
                        override fun onAnimationEnd(animation: Animation?) {
                            binding.loginIntroFragmentContainer.visibility = View.GONE
                            binding.loginIntroLayout.visibility = View.VISIBLE
                            supportFragmentManager.popBackStack()
                        }
                    })


                } else {
                    finish()
                }
            }
        }
        onBackPressedDispatcher.addCallback(this, callback)
        printKeyHash()
    }


    private fun loginToServerWithKakaoToken(kakaoAccessToken: String) {
        val authApi = RetrofitClient.getAuthApi(this)
        val state = UUID.randomUUID().toString()

        authApi.kakaoLogin(kakaoAccessToken, state).enqueue(object : Callback<KakaoLoginResponse> {
            override fun onResponse(
                call: Call<KakaoLoginResponse>,
                response: Response<KakaoLoginResponse>
            ) {
                if (response.isSuccessful) {
                    val backendResponse = response.body()
                    val userData = backendResponse?.data

                    if (backendResponse?.status == "OK" && userData != null) {

                        TokenManager.saveTokens(this@LoginIntroActivity, userData.accessToken, userData.refreshToken)
                        TokenManager.saveUserInfo(this@LoginIntroActivity, userData.email, userData.nickName, userData.profileImage)
                        TokenManager.saveLoginType(this@LoginIntroActivity, "KAKAO")

                        val intent = Intent(this@LoginIntroActivity, MainActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish()
                    } else {
                        Log.e(TAG, "백엔드 로그인 실패: ${response.code()} / ${backendResponse?.message}")
                        showSimpleConfirmDialog("카카오 로그인에 실패했습니다.\n(서버 오류: ${backendResponse?.message})")
                    }
                } else {
                    if (response.code() == 409) {
                        Log.e(TAG, "백엔드 카카오 로그인 실패: 409 Conflict")
                        showSimpleConfirmDialog("이미 가입된 계정입니다.\n다른 방법으로 로그인해주세요.")
                    } else {
                        Log.e(TAG, "백엔드 카카오 로그인 응답 실패: ${response.code()}")
                        showSimpleConfirmDialog("카카오 로그인에 실패했습니다.\n(응답 코드: ${response.code()})")
                    }
                }
            }

            override fun onFailure(call: Call<KakaoLoginResponse>, t: Throwable) {
                Log.e(TAG, "백엔드 로그인 네트워크 오류", t)
                showSimpleConfirmDialog("서버 통신에 실패했습니다.\n네트워크 상태를 확인해주세요.")
            }
        })
    }

    private val kakaoLoginCallback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
        if (error != null) {
            Log.e(TAG, "카카오 SDK 로그인 실패", error)
            Toast.makeText(this, "카카오 로그인에 실패했습니다. 다시 시도해주세요.", Toast.LENGTH_SHORT).show()
        } else if (token != null) {
            Log.i(TAG, "카카오 SDK 로그인 성공, AccessToken: ${token.accessToken}")
            loginToServerWithKakaoToken(token.accessToken)
        }
    }

    private fun startKakaoLogin() {
        if (UserApiClient.instance.isKakaoTalkLoginAvailable(this)) {
            UserApiClient.instance.loginWithKakaoTalk(this) { token, error ->
                if (error != null) {
                    Log.e(TAG, "카카오톡으로 로그인 실패", error)
                    if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                        return@loginWithKakaoTalk
                    }
                    UserApiClient.instance.loginWithKakaoAccount(this, callback = kakaoLoginCallback)
                } else if (token != null) {
                    Log.i(TAG, "카카오톡으로 로그인 성공 ${token.accessToken}")
                    kakaoLoginCallback(token, null)
                }
            }
        } else {
            UserApiClient.instance.loginWithKakaoAccount(this, callback = kakaoLoginCallback)
        }
    }

    private fun loginToServerWithNaverToken(naverAccessToken: String) {
        val authApi = RetrofitClient.getAuthApi(this)
        val state = UUID.randomUUID().toString()

        authApi.naverLogin(naverAccessToken, state).enqueue(object : Callback<NaverLoginResponse> {
            override fun onResponse(call: Call<NaverLoginResponse>, response: Response<NaverLoginResponse>) {
                if (response.isSuccessful) {
                    val backendResponse = response.body()
                    val userData = backendResponse?.data

                    if (backendResponse?.status == "OK" && userData != null) {
                        TokenManager.saveTokens(this@LoginIntroActivity, userData.accessToken, userData.refreshToken)
                        TokenManager.saveUserInfo(this@LoginIntroActivity, userData.email, userData.nickName, userData.profileImage)
                        TokenManager.saveLoginType(this@LoginIntroActivity, "NAVER")

                        val intent = Intent(this@LoginIntroActivity, MainActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish()
                    } else {
                        val errorMessage = backendResponse?.message ?: "Unknown server error"
                        Log.e(TAG, "백엔드 네이버 로그인 실패: ${response.code()} / $errorMessage")
                        showSimpleConfirmDialog("네이버 로그인에 실패했습니다.\n(서버 오류: $errorMessage)")
                    }
                } else {
                    if (response.code() == 409) {
                        Log.e(TAG, "백엔드 네이버 로그인 실패: 409 Conflict")
                        showSimpleConfirmDialog("이미 가입된 계정입니다.\n다른 방법으로 로그인해주세요.")
                    } else {
                        Log.e(TAG, "백엔드 네이버 로그인 응답 실패: ${response.code()}")
                        showSimpleConfirmDialog("네이버 로그인에 실패했습니다.\n(응답 코드: ${response.code()})")
                    }
                }
            }

            override fun onFailure(call: Call<NaverLoginResponse>, t: Throwable) {
                Log.e(TAG, "백엔드 네이버 로그인 네트워크 오류", t)
                showSimpleConfirmDialog("서버 통신에 실패했습니다.\n네트워크 상태를 확인해주세요.")
            }
        })
    }

    private val naverLoginCallback = object : OAuthLoginCallback {
        override fun onSuccess() {
            val accessToken = NaverIdLoginSDK.getAccessToken()
            if (!accessToken.isNullOrEmpty()) {
                loginToServerWithNaverToken(accessToken)
            } else {
                Log.e("NaverLogin", "accessToken 없음")
                Toast.makeText(this@LoginIntroActivity, "네이버 토큰을 가져오지 못했습니다.", Toast.LENGTH_SHORT).show()
            }
        }

        override fun onFailure(httpStatus: Int, message: String) {
            Log.e("NaverLogin", "로그인 실패 - HTTP $httpStatus: $message")
            Toast.makeText(this@LoginIntroActivity, "네이버 로그인에 실패했습니다.", Toast.LENGTH_SHORT).show()
        }

        override fun onError(errorCode: Int, message: String) {
            Log.e("NaverLogin", "로그인 오류 - Code $errorCode: $message")
            Toast.makeText(this@LoginIntroActivity, "네이버 로그인 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun startNaverLogin() {
        NaverIdLoginSDK.authenticate(this, naverLoginCallback)
    }

    private fun showSimpleConfirmDialog(
        message: String,
        buttonText: String = "확인",
        onButtonClick: (() -> Unit)? = null
    ) {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_confirm)
        dialog.window?.setBackgroundDrawable(ColorDrawable(android.graphics.Color.TRANSPARENT))
        dialog.window?.setDimAmount(0f)

        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )

        val tvMessage = dialog.findViewById<TextView>(R.id.tv_dialog_message)
        val btnOk = dialog.findViewById<TextView>(R.id.btn_dialog_ok)

        tvMessage.text = message
        btnOk.text = buttonText

        btnOk.setOnClickListener {
            dialog.dismiss()
            onButtonClick?.invoke()
        }

        dialog.show()
    }

    private fun printKeyHash() {
        try {
            val info = packageManager.getPackageInfo(packageName, android.content.pm.PackageManager.GET_SIGNATURES)
            for (signature in info.signatures!!) {
                val md = java.security.MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                val keyHash = android.util.Base64.encodeToString(md.digest(), android.util.Base64.NO_WRAP)
                Log.d("KeyHash", keyHash)
            }
        } catch (e: Exception) {
            Log.e("KeyHash", "키 해시 얻기 실패", e)
        }
    }
}