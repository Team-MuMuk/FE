package com.example.mumuk.ui.signup

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.mumuk.R
import com.example.mumuk.data.api.RetrofitClient
import com.example.mumuk.data.api.TokenManager
import com.example.mumuk.data.model.auth.LoginRequest
import com.example.mumuk.data.model.auth.LoginResponse
import com.example.mumuk.data.model.auth.SignupRequest
import com.example.mumuk.data.model.auth.SignupResponse
import com.example.mumuk.databinding.FragmentSignupCompleteBinding
import com.example.mumuk.ui.MainActivity
import com.example.mumuk.ui.health.HealthManagementFragment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignupCompleteFragment : Fragment() {

    private var _binding: FragmentSignupCompleteBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignupCompleteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val activity = requireActivity() as SignupActivity

        binding.btn.setOnClickListener {
            // 회원가입 API 호출
            val signupRequest = SignupRequest(
                name = activity.name,
                nickname = activity.nickname,
                phoneNumber = activity.phoneNumber,
                loginId = activity.loginId,
                password = activity.password,
                confirmPassword = activity.confirmPassword
            )

            RetrofitClient.getAuthApi(requireContext()).signUp(signupRequest)
                .enqueue(object : retrofit2.Callback<SignupResponse> {
                    override fun onResponse(call: Call<SignupResponse>, response: Response<SignupResponse>) {
                        if (response.isSuccessful) {
                            Log.d("SignupComplete", "회원가입 성공: ${response.body()?.message}")

                            // 회원가입 성공 후 로그인 API 호출
                            val loginRequest = LoginRequest(
                                loginId = activity.loginId,
                                password = activity.password
                            )

                            RetrofitClient.getAuthApi(requireContext()).login(loginRequest)
                                .enqueue(object : Callback<LoginResponse> {
                                    override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                                        if (response.isSuccessful) {
                                            val tokenData = response.body()?.data
                                            if (tokenData != null) {
                                                TokenManager.saveTokens(requireContext(), tokenData.accessToken, tokenData.refreshToken)
                                                Log.d("SignupComplete", "자동 로그인 성공")

                                                // MainActivity로 이동
                                                val intent = Intent(requireContext(), MainActivity::class.java)
                                                intent.putExtra("go_health_management", true)
                                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                                                startActivity(intent)
                                                requireActivity().finish()
                                            } else {
                                                Log.e("SignupComplete", "로그인 응답에 토큰 없음")
                                            }
                                        } else {
                                            Log.e("SignupComplete", "자동 로그인 실패: ${response.code()}")
                                        }
                                    }

                                    override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                                        Log.e("SignupComplete", "로그인 네트워크 오류: ${t.message}")
                                    }
                                })
                        } else {
                            Log.e("SignupComplete", "회원가입 실패: ${response.code()}")
                        }
                    }

                    override fun onFailure(call: Call<SignupResponse>, t: Throwable) {
                        Log.e("SignupComplete", "회원가입 네트워크 오류: ${t.message}")
                    }
                })
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
