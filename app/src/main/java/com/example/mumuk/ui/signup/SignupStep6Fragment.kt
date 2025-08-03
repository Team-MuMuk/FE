package com.example.mumuk.ui.signup

import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.mumuk.R
import com.example.mumuk.data.api.RetrofitClient
import com.example.mumuk.data.api.TokenManager
import com.example.mumuk.data.model.auth.LoginRequest
import com.example.mumuk.data.model.auth.LoginResponse
import com.example.mumuk.data.model.auth.SignupRequest
import com.example.mumuk.data.model.auth.SignupResponse
import com.example.mumuk.databinding.FragmentSignupStep6Binding
import com.example.mumuk.ui.MainActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignupStep6Fragment : Fragment() {

    private var _binding: FragmentSignupStep6Binding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignupStep6Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val activity = requireActivity() as SignupActivity
        val originalPassword = activity.password

        binding.etPw.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val input = s.toString()

                when {
                    input.isEmpty() -> {
                        binding.ivPwStatusIcon.setImageResource(R.drawable.ic_error)
                        binding.ivPwStatusIcon.visibility = View.VISIBLE
                        binding.tvPwStatus.text = "비밀번호를 입력해주세요."
                        binding.tvPwStatus.setTextColor(ContextCompat.getColor(requireContext(), R.color.red))
                        binding.layoutPw.error = null
                    }
                    input != originalPassword -> {
                        binding.ivPwStatusIcon.setImageResource(R.drawable.ic_error)
                        binding.ivPwStatusIcon.visibility = View.VISIBLE
                        binding.tvPwStatus.text = "비밀번호가 일치하지 않습니다."
                        binding.tvPwStatus.setTextColor(ContextCompat.getColor(requireContext(), R.color.red))
                        binding.layoutPw.error = null
                    }
                    else -> {
                        binding.ivPwStatusIcon.setImageResource(R.drawable.ic_check)
                        binding.ivPwStatusIcon.visibility = View.VISIBLE
                        binding.tvPwStatus.text = "비밀번호가 일치합니다"
                        binding.tvPwStatus.setTextColor(Color.parseColor("#306AF2"))
                        binding.layoutPw.error = null
                    }
                }
            }
        })

        binding.btnNext.setOnClickListener {
            val confirmPw = binding.etPw.text.toString()
            val statusText = binding.tvPwStatus.text.toString()

            if (statusText == "비밀번호가 일치합니다") {
                activity.confirmPassword = confirmPw

                val request = SignupRequest(
                    name = activity.name,
                    nickname = activity.nickname,
                    phoneNumber = activity.phoneNumber,
                    loginId = activity.loginId,
                    password = activity.password,
                    confirmPassword = activity.confirmPassword
                )

                Log.d("Signup", "📦 요청 객체: $request")

                RetrofitClient.getAuthApi(requireContext()).signUp(request)
                    .enqueue(object : retrofit2.Callback<SignupResponse> {
                        override fun onResponse(
                            call: Call<SignupResponse>,
                            response: Response<SignupResponse>
                        ) {
                            Log.d("Signup", "📡 응답 코드: ${response.code()}")

                            if (response.isSuccessful) {
                                val body = response.body()
                                Log.d("Signup", "회원가입 성공: ${body?.message}")

                                // 회원가입 성공 후 바로 로그인 요청
                                val loginRequest = LoginRequest(
                                    loginId = activity.loginId,
                                    password = activity.password
                                )

                                RetrofitClient.getAuthApi(requireContext()).login(loginRequest)
                                    .enqueue(object : Callback<LoginResponse> {
                                        override fun onResponse(
                                            call: Call<LoginResponse>,
                                            response: Response<LoginResponse>
                                        ) {
                                            if (response.isSuccessful) {
                                                val loginBody = response.body()
                                                val tokenData = loginBody?.data

                                                if (tokenData != null) {
                                                    val accessToken = tokenData.accessToken
                                                    val refreshToken = tokenData.refreshToken
                                                    TokenManager.saveTokens(requireContext(), accessToken, refreshToken)
                                                    Log.d("Login", "자동 로그인 성공 - accessToken: $accessToken")

                                                    // MainActivity로 이동
                                                    parentFragmentManager.beginTransaction()
                                                        .replace(R.id.signup_container, SignupCompleteFragment())
                                                        .addToBackStack(null)
                                                        .commit()
                                                } else {
                                                    Log.e("Login", "로그인 응답에 토큰 없음")
                                                }
                                            } else {
                                                Log.e("Login", "자동 로그인 실패: ${response.code()}")
                                            }
                                        }

                                        override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                                            Log.e("Login", "자동 로그인 네트워크 오류: ${t.message}")
                                        }
                                    })
                            } else {
                                Log.e("Signup", "회원가입 실패: ${response.code()}")
                                val errorBody = response.errorBody()?.string()
                                Log.e("Signup", "에러 내용: $errorBody")

                                try {
                                    val jsonObject = org.json.JSONObject(errorBody)
                                    val message = jsonObject.optString("message", "회원가입에 실패했습니다.")
                                    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                                } catch (e: Exception) {
                                    Toast.makeText(requireContext(), "회원가입에 실패했습니다.", Toast.LENGTH_SHORT).show()
                                }
                            }

                        }

                        override fun onFailure(call: Call<SignupResponse>, t: Throwable) {
                            Log.e("Signup", "네트워크 오류: ${t.message}")
                        }
                    })
            }
        }



        binding.btnBack.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.signup_container, SignupStep5Fragment())
                .addToBackStack(null)
                .commit()
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
