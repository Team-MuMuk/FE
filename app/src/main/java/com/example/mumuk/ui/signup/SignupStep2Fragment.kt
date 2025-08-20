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
import androidx.navigation.fragment.findNavController
import com.example.mumuk.R
import com.example.mumuk.data.api.RetrofitClient
import com.example.mumuk.data.model.auth.CommonResponse
import com.example.mumuk.databinding.FragmentSignupStep2Binding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignupStep2Fragment : Fragment() {

    private var _binding: FragmentSignupStep2Binding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignupStep2Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnNext.setImageResource(R.drawable.btn_next_gray)

        binding.etNickname.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val nickname = s.toString()

                if (nickname.isBlank()) {
                    binding.btnNext.isEnabled = true
                    binding.btnNext.setImageResource(R.drawable.btn_next_gray)
                    setErrorStatus("닉네임을 입력해주세요.")
                } else if (nickname.length >= 10) {
                    binding.btnNext.isEnabled = true
                    binding.btnNext.setImageResource(R.drawable.btn_next_gray)
                    setErrorStatus("글자 수가 초과되었습니다. 10자 이내로 입력해주세요.")
                } else {
                    binding.btnNext.isEnabled = true
                    binding.btnNext.setImageResource(R.drawable.btn_next)
                    checkNicknameDuplicate(nickname)
                }
            }
        })

        binding.btnNext.setOnClickListener {
            val nickname = binding.etNickname.text.toString()

            when {
                nickname.isBlank() -> {
                    setErrorStatus("닉네임을 입력해주세요.")
                }
                nickname.length >= 10 -> {
                    setErrorStatus("글자 수가 초과되었습니다. 10자 이내로 입력해주세요.")
                }
                else -> {
                    (requireActivity() as SignupActivity).nickname = nickname
                    findNavController().navigate(R.id.action_step2_to_step3)

                }
            }
        }

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()

        }
    }

    private fun setErrorStatus(message: String) {
        binding.tvNicknameStatus.text = message
        binding.tvNicknameStatus.setTextColor(ContextCompat.getColor(requireContext(), R.color.red))
        binding.ivNicknameStatusIcon.setImageResource(R.drawable.ic_error)
        binding.ivNicknameStatusIcon.visibility = View.VISIBLE
        binding.btnNext.setImageResource(R.drawable.btn_next_gray)
        binding.btnNext.isEnabled = false
    }

    private fun setSuccessStatus(message: String) {
        binding.tvNicknameStatus.text = message
        binding.tvNicknameStatus.setTextColor(Color.parseColor("#306AF2"))
        binding.ivNicknameStatusIcon.setImageResource(R.drawable.ic_check)
        binding.ivNicknameStatusIcon.visibility = View.VISIBLE
        binding.btnNext.setImageResource(R.drawable.btn_next)
        binding.btnNext.isEnabled = true
    }

    private fun checkNicknameDuplicate(nickname: String) {
        RetrofitClient.getAuthApi(requireContext()).checkNicknameExists(nickname)
            .enqueue(object : Callback<CommonResponse> {
                override fun onResponse(call: Call<CommonResponse>, response: Response<CommonResponse>) {
                    if (response.isSuccessful) {
                        val message = response.body()?.data?.toString() ?: ""
                        Log.d("NicknameCheck", "응답 메시지: $message")

                        when {
                            message.contains("사용 가능") -> {
                                setSuccessStatus("사용 가능한 닉네임입니다.")
                            }
                            message.contains("이미 사용") -> {
                                setErrorStatus("이미 존재하는 닉네임입니다. 다시 입력해주세요.")
                            }
                            else -> {
                                setErrorStatus("응답을 이해할 수 없습니다.")
                            }
                        }
                    } else {
                        Log.e("NicknameCheck", "응답 실패: ${response.code()} - ${response.errorBody()?.string()}")
                        setErrorStatus("서버 오류가 발생했습니다.")
                    }
                }

                override fun onFailure(call: Call<CommonResponse>, t: Throwable) {
                    Log.e("NicknameCheck", "네트워크 오류: ${t.message}")
                    setErrorStatus("네트워크 오류가 발생했습니다.")
                }
            })
    }




    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

