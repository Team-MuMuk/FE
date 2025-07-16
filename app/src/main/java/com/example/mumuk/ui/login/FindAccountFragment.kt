package com.example.mumuk.ui.login

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.example.mumuk.R
import com.example.mumuk.databinding.FragmentFindAccountBinding

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
            showCustomDialog(
                message = "인증코드가 확인되었습니다.",
                layoutResId = R.layout.dialog_confirm,
                buttonText = "아이디 확인하기"
            ) {
                showIdDialog("mumuk123")
            }
        }

        binding.btnSendCodePw.setOnClickListener {
            showCustomDialog("임시 비밀번호가 발송되었습니다.", R.layout.dialog_code_sent)
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
