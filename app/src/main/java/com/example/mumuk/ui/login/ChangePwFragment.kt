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
import com.example.mumuk.databinding.FragmentChangePwBinding

class ChangePwFragment : Fragment() {

    private var _binding: FragmentChangePwBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChangePwBinding.inflate(inflater, container, false)
        return binding.root


    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
                showPasswordChangedDialog()
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
            startActivity(Intent(requireContext(), LoginActivity::class.java))
            requireActivity().finish()
        }

        dialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
