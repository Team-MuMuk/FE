package com.example.mumuk.ui.login

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.mumuk.R
import com.example.mumuk.data.api.RetrofitClient
import com.example.mumuk.data.model.auth.ReissuePwRequest
import com.example.mumuk.data.model.auth.ReissuePwResponse
import com.example.mumuk.databinding.FragmentChangePwBinding
import com.example.mumuk.ui.mypage.MyPageFragment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SubChangePw2Fragment : Fragment() {

    private var _binding: FragmentChangePwBinding? = null
    private val binding get() = _binding!!
    private var currentPassword: String = ""


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChangePwBinding.inflate(inflater, container, false)
        return binding.root


    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        currentPassword = arguments?.getString("currentPassWord") ?: ""


        binding.btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        binding.btnConfirmChangePw.setOnClickListener {
            val pw = binding.etPw.text.toString()
            val pwNew = binding.etPwNew.text.toString()


            val pwPattern =
                Regex("^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@\$!%*#?&])[A-Za-z\\d@\$!%*#?&]{8,15}$")

            if (!pwPattern.matches(pw)) {
                binding.tvPwFormatStatus.apply {
                    text = "비밀번호 형식에 맞지 않습니다. 영문자, 특수문자, 숫자 조합으로 작성해주세요"
                    setTextColor(resources.getColor(R.color.red, null))
                }
                binding.tvPwStatus.text = ""
                return@setOnClickListener
            } else {
                binding.tvPwFormatStatus.apply {
                    text = "(영문자,숫자,특수문자 포함 8자이상 15자 이하)"
                    setTextColor(Color.parseColor("#A2A2A2"))
                }
            }

            if (pw != pwNew) {
                binding.tvPwStatus.text = "비밀번호가 일치하지 않습니다"
            } else {
                binding.tvPwStatus.text = ""

                val request = ReissuePwRequest(
                    currentPassWord = currentPassword,
                    passWord = pw,
                    confirmPassWord = pwNew
                )


                RetrofitClient.getAuthApi(requireContext()).reissuePassword(request)
                    .enqueue(object : Callback<ReissuePwResponse> {
                        override fun onResponse(
                            call: Call<ReissuePwResponse>,
                            response: Response<ReissuePwResponse>
                        ) {
                            val result = response.body()
                            if (response.isSuccessful && result?.message?.contains("성공") == true) {
                                showPasswordChangedDialog()
                            } else {
                                binding.tvPwStatus.text = "변경 실패: ${result?.message ?: "서버 응답 없음"}"
                            }

                        }

                        override fun onFailure(call: Call<ReissuePwResponse>, t: Throwable) {
                            binding.tvPwStatus.text = "네트워크 오류: ${t.message}"
                        }
                    })
            }

        }

    }

    private fun showPasswordChangedDialog() {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_pw_changed)

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog.window?.setGravity(Gravity.CENTER)

        val btnOk = dialog.findViewById<TextView>(R.id.btn_dialog_ok)
        btnOk?.setOnClickListener {
            dialog.dismiss()

            parentFragmentManager.beginTransaction()
                .replace(R.id.signup_container, MyPageFragment())
                .addToBackStack(null)
                .commit()
        }


        dialog.show()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
