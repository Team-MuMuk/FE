package com.example.mumuk.ui.login

import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.example.mumuk.R
import com.example.mumuk.data.api.RetrofitClient
import com.example.mumuk.data.model.auth.CommonResponse
import com.example.mumuk.data.model.auth.FindIdRequest
import com.example.mumuk.data.model.auth.FindPwRequest
import com.example.mumuk.databinding.FragmentFindAccountBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FindAccountFragment : Fragment() {

    private var _binding: FragmentFindAccountBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFindAccountBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        showFindIdLayout()

        binding.btnBack.setOnClickListener {
            val currentActivity = requireActivity()
            if (currentActivity is LoginActivity) {
                currentActivity.findViewById<View>(R.id.login_layout).visibility = View.VISIBLE
                currentActivity.findViewById<View>(R.id.login_fragment_container).visibility = View.GONE
            } else if (currentActivity is LoginIntroActivity) {
                currentActivity.findViewById<View>(R.id.login_intro_layout).visibility = View.VISIBLE
                currentActivity.findViewById<View>(R.id.login_intro_fragment_container).visibility = View.GONE
            }
            currentActivity.supportFragmentManager.popBackStack()
        }

        binding.tabFindId.setOnClickListener { showFindIdLayout() }
        binding.tabFindPw.setOnClickListener { showFindPwLayout() }

        binding.btnSendCode.setOnClickListener {
            showCustomDialog("인증코드가 발송되었습니다.", R.layout.dialog_code_sent)
        }

        binding.btnConfirmCode.setOnClickListener {
            val name = binding.etName.text.toString().trim()
            val phoneNumber = binding.etNum.text.toString().trim()

            val request = FindIdRequest(name, phoneNumber)

            Log.d("FindId", "Request: name=$name, phone=$phoneNumber")

            RetrofitClient.getAuthApi(requireContext()).findId(request)
                .enqueue(object : Callback<CommonResponse> {
                    override fun onResponse(call: Call<CommonResponse>, response: Response<CommonResponse>) {
                        val result = response.body()

                        Log.d("FindId", "HTTP code: ${response.code()}")
                        Log.d("FindId", "Raw response: ${response.raw()}")
                        Log.d("FindId", "Response body: $result")

                        if (response.isSuccessful && result?.message?.contains("성공") == true) {
                            val userId = result.data ?: ""
                            Log.d("FindId", "아이디 찾기 성공 - userId: $userId")
                            showIdDialog(userId)
                        } else {
                            Log.d("FindId", "아이디 찾기 실패 - message: ${result?.message}")
                            showCustomDialog(
                                message = "아이디 찾기에 실패했습니다.\n입력 정보를 확인해 주세요.",
                                layoutResId = R.layout.dialog_confirm
                            )
                        }
                    }

                    override fun onFailure(call: Call<CommonResponse>, t: Throwable) {
                        Log.e("FindId", "네트워크 오류: ${t.message}", t)
                        showCustomDialog(
                            message = "네트워크 오류가 발생했습니다.\n다시 시도해 주세요.",
                            layoutResId = R.layout.dialog_confirm
                        )
                    }
                })
        }



        binding.btnSendCodePw.setOnClickListener {
            binding.btnSendCodePw.setOnClickListener {
                val loginId = binding.etIdPw.text.toString().trim()
                val name = binding.etNamePw.text.toString().trim()
                val phone = binding.etPhonePw.text.toString().trim()

                val request = FindPwRequest(loginId, name, phone)

                RetrofitClient.getAuthApi(requireContext()).findPassword(request)
                    .enqueue(object : Callback<CommonResponse> {
                        override fun onResponse(call: Call<CommonResponse>, response: Response<CommonResponse>) {
                            Log.d("FindPwResponse", "response: ${response.body()}")
                            Log.d("FindPwResponse", "code: ${response.code()}, msg: ${response.message()}")

                            val result = response.body()
                            if (response.isSuccessful && result?.message?.contains("성공") == true) {
                                showCustomDialog(
                                    message = "임시 비밀번호가 발송되었습니다.",
                                    layoutResId = R.layout.dialog_code_sent
                                )
                            } else {
                                showCustomDialog(
                                    message = "비밀번호 찾기에 실패했습니다.\n입력 정보를 확인해 주세요.",
                                    layoutResId = R.layout.dialog_confirm
                                )
                            }
                        }


                        override fun onFailure(call: Call<CommonResponse>, t: Throwable) {
                            showCustomDialog("네트워크 오류가 발생했습니다.\n다시 시도해 주세요.", R.layout.dialog_confirm)
                        }
                    })
            }

        }


        binding.btnConfirmCodePw.setOnClickListener {
            showCustomDialog(
                message = "임시 비밀번호가 설정되었습니다.",
                layoutResId = R.layout.dialog_confirm,
                buttonText = "새 비밀번호 만들기"
            ) {
                val containerId = when (requireActivity()) {
                    is LoginActivity -> R.id.login_fragment_container
                    is LoginIntroActivity -> R.id.login_intro_fragment_container
                    else -> throw IllegalStateException("알 수 없는 액티비티입니다.")
                }

                parentFragmentManager.beginTransaction()
                    .replace(containerId, ChangePwFragment())
                    .addToBackStack(null)
                    .commit()
            }
        }





    }

    private fun showFindIdLayout() {
        binding.layoutFindId.visibility = View.VISIBLE
        binding.layoutFindPw.visibility = View.GONE

        binding.tabFindId.setTextColor(resources.getColor(R.color.black))
        binding.tabFindPw.setTextColor(Color.parseColor("#A2A2A2"))

        val params = binding.subline.layoutParams as ConstraintLayout.LayoutParams
        params.startToStart = R.id.line
        params.endToEnd = ConstraintLayout.LayoutParams.UNSET
        binding.subline.layoutParams = params
    }

    private fun showFindPwLayout() {
        binding.layoutFindId.visibility = View.GONE
        binding.layoutFindPw.visibility = View.VISIBLE

        binding.tabFindPw.setTextColor(resources.getColor(R.color.black))
        binding.tabFindId.setTextColor(Color.parseColor("#A2A2A2"))

        val params = binding.subline.layoutParams as ConstraintLayout.LayoutParams
        params.endToEnd = R.id.line
        params.startToStart = ConstraintLayout.LayoutParams.UNSET
        binding.subline.layoutParams = params
    }


    fun showCustomDialog(
        message: String,
        layoutResId: Int,
        buttonText: String = "확인",
        onButtonClick: (() -> Unit)? = null
    ) {
        val dialog = Dialog(requireContext())
        dialog.setContentView(layoutResId)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        val tvMessage = dialog.findViewById<TextView>(R.id.tv_dialog_message)
        tvMessage.text = message

        val btnOk = dialog.findViewById<TextView>(R.id.btn_dialog_ok)
        btnOk.text = buttonText
        btnOk.setOnClickListener {
            dialog.dismiss()
            onButtonClick?.invoke()
        }

        dialog.show()
    }

    fun showIdDialog(userId: String) {
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.dialog_show_id)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        val tvMessage = dialog.findViewById<TextView>(R.id.tv_dialog_message)
        tvMessage.text = "아이디는\n$userId 입니다."

        val btnOk = dialog.findViewById<TextView>(R.id.btn_dialog_ok)
        btnOk.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
