package com.example.mumuk.ui.signup

import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.mumuk.R
import com.example.mumuk.data.api.RetrofitClient
import com.example.mumuk.data.model.auth.SignupRequest
import com.example.mumuk.data.model.auth.SignupResponse
import com.example.mumuk.databinding.FragmentSignupStep6Binding
import retrofit2.Call
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

                RetrofitClient.getAuthApi(requireContext()).signUp(request)
                    .enqueue(object : retrofit2.Callback<com.example.mumuk.data.model.auth.SignupResponse> {
                        override fun onResponse(
                            call: Call<SignupResponse>,
                            response: Response<SignupResponse>
                        ) {
                            if (response.isSuccessful) {
                                val body = response.body()
                                Log.d("Signup", "회원가입 성공: ${body?.message}")
                                // 성공하면 완료 화면으로 이동
                                parentFragmentManager.beginTransaction()
                                    .replace(R.id.signup_container, SignupCompleteFragment())
                                    .addToBackStack(null)
                                    .commit()
                            } else {
                                Log.e("Signup", "회원가입 실패: ${response.code()}")

                            }
                        }

                        override fun onFailure(
                            call: retrofit2.Call<com.example.mumuk.data.model.auth.SignupResponse>,
                            t: Throwable
                        ) {
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
