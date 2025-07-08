package com.example.mumuk

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.example.mumuk.databinding.FragmentChangePwBinding
import com.example.mumuk.ui.login.FindIdFragment
import com.example.mumuk.ui.login.FindPwFragment
import com.example.mumuk.ui.login.LoginActivity

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
            startActivity(Intent(requireContext(), LoginActivity::class.java))
            requireActivity().finish()
        }

        binding.tabFindId.setOnClickListener {
            parentFragmentManager.commit {
                replace(R.id.login_fragment_container, FindIdFragment())
                addToBackStack(null)
            }
        }

        binding.tabFindPw.setOnClickListener {
            parentFragmentManager.commit {
                replace(R.id.login_fragment_container, FindPwFragment())
                addToBackStack(null)
            }
        }

        binding.btnConfirmCode.setOnClickListener {
            val pw = binding.etPw.text.toString()
            val pwNew = binding.etPwNew.text.toString()

            val pwPattern =
                Regex("^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@\$!%*#?&])[A-Za-z\\d@\$!%*#?&]{3,}$")

            if (!pwPattern.matches(pw)) {
                binding.tvPwFormatStatus.text =
                    "비밀번호 형식에 맞지 않습니다. 영문자, 특수문자, 숫자 조합으로 작성해주세요"
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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
        btnOk.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }
}
