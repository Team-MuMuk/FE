package com.example.mumuk

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.example.mumuk.Login.FindIdFragment
import com.example.mumuk.Login.FindPwFragment
import com.example.mumuk.Login.LoginActivity

class ChangePwFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_change_pw, container, false)


        val btnBack = view.findViewById<ImageView>(R.id.btn_back)
        val tabFindId = view.findViewById<TextView>(R.id.tab_find_id)
        val tabFindPw = view.findViewById<TextView>(R.id.tab_find_pw)
        

        val etPw = view.findViewById<EditText>(R.id.et_pw)
        val etPwNew = view.findViewById<EditText>(R.id.et_pw_new)
        val btnConfirm = view.findViewById<AppCompatButton>(R.id.btn_confirm_code)
        val tvPwStatus = view.findViewById<TextView>(R.id.tv_pw_status)
        val tvPwFormatStatus = view.findViewById<TextView>(R.id.tv_pw_format_status)


        btnBack.setOnClickListener {
            startActivity(Intent(requireContext(), LoginActivity::class.java))
            requireActivity().finish()
        }

        tabFindId.setOnClickListener {
            parentFragmentManager.commit {
                replace(R.id.login_fragment_container, FindIdFragment())
                addToBackStack(null)
            }
        }

        tabFindPw.setOnClickListener {
            parentFragmentManager.commit {
                replace(R.id.login_fragment_container, FindPwFragment())
                addToBackStack(null)
            }
        }

        btnConfirm.setOnClickListener {
            val pw = etPw.text.toString()
            val pwNew = etPwNew.text.toString()

            val pwPattern = Regex("^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@\$!%*#?&])[A-Za-z\\d@\$!%*#?&]{3,}$")

            if (!pwPattern.matches(pw)) {
                tvPwFormatStatus.text = "비밀번호 형식에 맞지 않습니다. 영문자, 특수문자, 숫자 조합으로 작성해주세요"
                tvPwStatus.text = ""
                return@setOnClickListener
            } else {
                tvPwFormatStatus.text = ""
            }

            if (pw != pwNew) {
                tvPwStatus.text = "비밀번호가 일치하지 않습니다"
                return@setOnClickListener
            } else {
                tvPwStatus.text = ""
                showPasswordChangedDialog()
            }
        }

        return view
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
            // TODO: 필요하면 로그인 화면으로 이동 등의 추가 로직
        }

        dialog.show()
    }
}
