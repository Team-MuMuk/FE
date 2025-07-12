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
            showCustomDialog("인증코드가 확인되었습니다.", R.layout.dialog_confirm)
        }

        binding.btnSendCodePw.setOnClickListener {
            showCustomDialog("인증코드가 발송되었습니다.", R.layout.dialog_code_sent)
        }

        // ✅ 비밀번호 찾기 > 인증 확인 → 비밀번호 변경 화면으로 전환
        binding.btnConfirmCodePw.setOnClickListener {
            showCustomDialog("인증코드가 확인되었습니다.", R.layout.dialog_confirm) {
                showChangePwLayout()
            }
        }

        // ✅ 비밀번호 변경 완료 처리
        binding.btnConfirmChangePw.setOnClickListener {
            val pw = binding.etPw.text.toString()
            val pwNew = binding.etPwNew.text.toString()

            val pwPattern =
                Regex("^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@\$!%*#?&])[A-Za-z\\d@\$!%*#?&]{3,}$")

            if (!pwPattern.matches(pw)) {
                binding.tvPwFormatStatus.text = "비밀번호 형식에 맞지 않습니다. 영문자, 특수문자, 숫자 조합으로 작성해주세요"
                binding.tvPwStatus.text = ""
                return@setOnClickListener
            } else {
                binding.tvPwFormatStatus.text = ""
            }

            if (pw != pwNew) {
                binding.tvPwStatus.text = "비밀번호가 일치하지 않습니다"
                return@setOnClickListener
            } else {
                binding.tvPwStatus.text = ""
                showPasswordChangedDialog()
            }
        }
    }

    private fun showFindIdLayout() {
        binding.layoutFindId.visibility = View.VISIBLE
        binding.layoutFindPw.visibility = View.GONE
        binding.layoutChangePw.visibility = View.GONE

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
        binding.layoutChangePw.visibility = View.GONE

        binding.tabFindPw.setTextColor(resources.getColor(R.color.black))
        binding.tabFindId.setTextColor(Color.parseColor("#A2A2A2"))

        val params = binding.subline.layoutParams as ConstraintLayout.LayoutParams
        params.endToEnd = R.id.line
        params.startToStart = ConstraintLayout.LayoutParams.UNSET
        binding.subline.layoutParams = params
    }

    private fun showChangePwLayout() {
        binding.layoutFindId.visibility = View.GONE
        binding.layoutFindPw.visibility = View.GONE
        binding.layoutChangePw.visibility = View.VISIBLE
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
            startActivity(Intent(requireContext(), LoginActivity::class.java))
            requireActivity().finish()
        }

        dialog.show()
    }

    private fun showCustomDialog(message: String, layoutResId: Int, onOk: (() -> Unit)? = null) {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(layoutResId)

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog.window?.setGravity(Gravity.CENTER)

        val tvMessage = dialog.findViewById<TextView>(R.id.tv_dialog_message)
        val btnOk = dialog.findViewById<TextView>(R.id.btn_dialog_ok)

        tvMessage?.text = message
        btnOk?.setOnClickListener {
            dialog.dismiss()
            onOk?.invoke()
        }

        dialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
