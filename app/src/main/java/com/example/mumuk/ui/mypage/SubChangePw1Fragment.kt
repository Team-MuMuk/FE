package com.example.mumuk.ui.mypage

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.mumuk.R
import com.example.mumuk.data.api.RetrofitClient
import com.example.mumuk.data.api.TokenManager
import com.example.mumuk.data.model.auth.ReissuePwRequest
import com.example.mumuk.data.model.auth.CommonResponse
import com.example.mumuk.data.model.auth.ReissuePwResponse
import com.example.mumuk.ui.login.SubChangePw2Fragment
import com.example.mumuk.utils.JwtUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SubChangePw1Fragment : Fragment() {

    private lateinit var editCurrentPw: EditText

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_sub_change_pw1, container, false)

        editCurrentPw = view.findViewById(R.id.et_pw_new)

        val confirmButton = view.findViewById<View>(R.id.btn_confirm_change_pw)
        confirmButton.setOnClickListener {
            verifyCurrentPassword()
        }

        val backButton = view.findViewById<View>(R.id.btn_back)
        backButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        return view
    }

    private fun verifyCurrentPassword() {
        val currentPw = editCurrentPw.text.toString()
        val accessToken = TokenManager.getAccessToken(requireContext())
        val userId = JwtUtils.getUserIdFromToken(accessToken ?: "")

        if (currentPw.isBlank() || userId == null) {
            Toast.makeText(requireContext(), "비밀번호를 입력해주세요", Toast.LENGTH_SHORT).show()
            return
        }

        val dummyPassword = "Valid123!@#" // 유효한 더미 패스워드
        val request = ReissuePwRequest(
            currentPassWord = currentPw,
            passWord = dummyPassword,
            confirmPassWord = dummyPassword
        )

        RetrofitClient.getAuthApi(requireContext()).reissuePassword(request)
            .enqueue(object : Callback<ReissuePwResponse> {
                override fun onResponse(call: Call<ReissuePwResponse>, response: Response<ReissuePwResponse>) {
                    if (response.isSuccessful && response.body()?.message?.contains("성공") == true) {
                        Log.d("SubChangePw1", "✅ 현재 비밀번호 일치")
                        showConfirmDialog()
                    } else {
                        Log.e("SubChangePw1", "현재 비밀번호 불일치 또는 응답 에러: ${response.code()} / ${response.message()}")
                        Toast.makeText(requireContext(), "비밀번호가 일치하지 않습니다", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<ReissuePwResponse>, t: Throwable) {
                    Log.e("SubChangePw1", "비밀번호 검증 API 실패", t)
                    Toast.makeText(requireContext(), "서버 연결 실패", Toast.LENGTH_SHORT).show()
                }
            })


    }

    private fun showConfirmDialog() {
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.dialog_confirm)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val tvMessage = dialog.findViewById<TextView>(R.id.tv_dialog_message)
        val btnOk = dialog.findViewById<TextView>(R.id.btn_dialog_ok)

        tvMessage.text = "확인되었습니다"
        btnOk.text = "확인"

        btnOk.setOnClickListener {
            dialog.dismiss()

            val fragment = SubChangePw2Fragment().apply {
                arguments = Bundle().apply {
                    putString("currentPassWord", editCurrentPw.text.toString())
                }
            }

            parentFragmentManager.beginTransaction()
                .replace(R.id.signup_container, fragment)
                .addToBackStack(null)
                .commit()
        }


        dialog.show()
    }
}
