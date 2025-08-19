package com.example.mumuk.ui.mypage

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.mumuk.R
import com.example.mumuk.data.api.RetrofitClient
import com.example.mumuk.data.model.auth.CheckCurrentPwRequest
import com.example.mumuk.data.model.auth.CommonResponse
import com.example.mumuk.databinding.FragmentSubChangePw1Binding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SubChangePw1Fragment : Fragment() {

    private var _binding: FragmentSubChangePw1Binding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d("SubChangePw1", "onCreateView 진입함")
        _binding = FragmentSubChangePw1Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("SubChangePw1", "onViewCreated 진입함")

        binding.btnConfirmChangePw.setOnClickListener { verifyCurrentPassword() }

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun verifyCurrentPassword() {
        val currentPw = binding.etPwNew.text.toString()
        if (currentPw.isBlank()) {
            Toast.makeText(requireContext(), "비밀번호를 입력해주세요", Toast.LENGTH_SHORT).show()
            return
        }

        val request = CheckCurrentPwRequest(currentPassword = currentPw)

        RetrofitClient.getAuthApi(requireContext()).checkCurrentPassword(request)
            .enqueue(object : Callback<CommonResponse> {
                override fun onResponse(call: Call<CommonResponse>, response: Response<CommonResponse>) {
                    val body = response.body()
                    Log.d("SubChangePw1", "응답 바디: $body")

                    if (response.isSuccessful && body?.status == "OK" && body.code == "USER_200") {
                        Log.d("SubChangePw1", "현재 비밀번호 확인 성공")
                        showConfirmDialog()
                    } else {
                        Log.e("SubChangePw1", "비밀번호 확인 실패: code=${body?.code}, message=${body?.message}")
                        Toast.makeText(requireContext(), body?.message ?: "비밀번호가 일치하지 않습니다", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<CommonResponse>, t: Throwable) {
                    Log.e("SubChangePw1", "비밀번호 확인 API 실패", t)
                    Toast.makeText(requireContext(), "서버 연결 실패", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun showConfirmDialog() {
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.dialog_confirm)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.setDimAmount(0f)
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )

        val tvMessage = dialog.findViewById<TextView>(R.id.tv_dialog_message)
        val btnOk = dialog.findViewById<TextView>(R.id.btn_dialog_ok)

        tvMessage.text = "확인되었습니다"
        btnOk.text = "확인"

        btnOk.setOnClickListener {
            dialog.dismiss()

            val args = bundleOf(
                "currentPassWord" to binding.etPwNew.text.toString()
            )
            if (isAdded) {
                findNavController().navigate(R.id.action_subChangePw1_to_subChangePw2, args)
            }
        }

        dialog.show()
    }
}
