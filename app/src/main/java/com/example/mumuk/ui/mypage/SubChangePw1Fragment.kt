package com.example.mumuk.ui.mypage

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.mumuk.R
import com.example.mumuk.ui.login.SubChangePw2Fragment

class SubChangePw1Fragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_sub_change_pw1, container, false)

        val confirmButton = view.findViewById<View>(R.id.btn_confirm_change_pw)
        confirmButton.setOnClickListener {
            showConfirmDialog()
        }

        val backButton = view.findViewById<View>(R.id.btn_back)
        backButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        return view
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

            // SubChangePw2Fragment로 전환
            parentFragmentManager.beginTransaction()
                .replace(R.id.signup_container, SubChangePw2Fragment())
                .addToBackStack(null)
                .commit()
        }

        dialog.show()
    }
}
