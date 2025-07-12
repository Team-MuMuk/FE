package com.example.mumuk.ui.signup

import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.mumuk.R
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class SignupStep5Fragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_signup_step5, container, false)

        val btnNext = view.findViewById<ImageButton>(R.id.btn_next)
        val btnBack = view.findViewById<ImageButton>(R.id.btn_back)

        val etPw = view.findViewById<TextInputEditText>(R.id.et_pw)
        val layoutPw = view.findViewById<TextInputLayout>(R.id.layout_pw)
        val tvStatus = view.findViewById<TextView>(R.id.tv_pw_status)

        val password = arguments?.getString("password") ?: ""

        etPw.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val input = s.toString()
                if (input.isEmpty()) {
                    tvStatus.text = "비밀번호를 입력해주세요."
                    tvStatus.setTextColor(ContextCompat.getColor(requireContext(), R.color.red))
                    layoutPw.error = null
                } else if (input != password) {
                    tvStatus.text = "비밀번호가 일치하지 않습니다."
                    tvStatus.setTextColor(ContextCompat.getColor(requireContext(), R.color.red))
                    layoutPw.error = null
                } else {
                    tvStatus.text = "비밀번호가 일치합니다 :)"
                    tvStatus.setTextColor(Color.parseColor("#306AF2"))
                    layoutPw.error = null
                }
            }
        })

        btnNext.setOnClickListener {
            val input = etPw.text.toString()
            val statusText = tvStatus.text.toString()

            if (statusText == "비밀번호가 일치합니다 :)") {
                parentFragmentManager.beginTransaction()
                    .replace(R.id.signup_container, SignupCompleteFragment())
                    .addToBackStack(null)
                    .commit()
            }
        }


        btnBack.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.signup_container, SignupStep4Fragment())
                .addToBackStack(null)
                .commit()
        }

        return view
    }
}
