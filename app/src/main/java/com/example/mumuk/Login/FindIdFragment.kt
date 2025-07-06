package com.example.mumuk.Login

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import android.content.Intent
import com.example.mumuk.R

class FindIdFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_find_id, container, false)

        val btnBack = view.findViewById<ImageView>(R.id.btn_back)
        val tabFindId = view.findViewById<TextView>(R.id.tab_find_id)
        val tabFindPw = view.findViewById<TextView>(R.id.tab_find_pw)

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

        val btnSendCode = view.findViewById<AppCompatButton>(R.id.btn_send_code)
        btnSendCode.setOnClickListener {
            showCustomDialog("인증코드가 발송되었습니다.", R.layout.dialog_code_sent)
        }

        val btnConfirmCode = view.findViewById<AppCompatButton>(R.id.btn_confirm_code)
        btnConfirmCode.setOnClickListener {
            showCustomDialog("인증코드가 확인되었습니다.", R.layout.dialog_confirm)
        }

        return view
    }

    private fun showCustomDialog(message: String, layoutResId: Int) {
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

        tvMessage.text = message
        btnOk.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }
}

