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

class SignupStep1Fragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_signup_step1, container, false)

        val btnNext = view.findViewById<ImageButton>(R.id.btn_next)
        val btnBack = view.findViewById<ImageButton>(R.id.btn_back)

        val etName = view.findViewById<TextInputEditText>(R.id.et_name)
        val layoutName = view.findViewById<TextInputLayout>(R.id.layout_name)
        val tvStatus = view.findViewById<TextView>(R.id.tv_name_status)

        etName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val nickname = s.toString()

                when {
                    nickname.isBlank() -> {
                        tvStatus.text = "이름을 입력해주세요."
                        tvStatus.setTextColor(ContextCompat.getColor(requireContext(), R.color.red))
                    }
                    nickname.length >= 10 -> {
                        tvStatus.text = "글자 수가 초과되었습니다. 10자 이내로 입력해주세요."
                        tvStatus.setTextColor(ContextCompat.getColor(requireContext(), R.color.red))
                    }
                    else -> {
                        tvStatus.text = "정상적으로 확인되었습니다 :)"
                        tvStatus.setTextColor(Color.parseColor("#306AF2"))
                    }
                }
            }
        })

        btnNext.setOnClickListener {
            val nickname = etName.text.toString()

            if (nickname.isNotBlank() && nickname.length < 10) {
                parentFragmentManager.beginTransaction()
                    .replace(R.id.signup_container, SignupStep2Fragment())
                    .addToBackStack(null)
                    .commit()
            }
        }

        btnBack.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.signup_container, SignupStep0Fragment())
                .addToBackStack(null)
                .commit()
        }

        return view
    }
}
