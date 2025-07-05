package com.example.mumuk.Signup

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

class SignupStep3Fragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_signup_step3, container, false)

        val btnNext = view.findViewById<ImageButton>(R.id.btn_next)
        val btnBack = view.findViewById<ImageButton>(R.id.btn_back)

        val etId = view.findViewById<TextInputEditText>(R.id.et_id)
        val layoutId = view.findViewById<TextInputLayout>(R.id.layout_id)
        val tvStatus = view.findViewById<TextView>(R.id.tv_id_status)

        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$".toRegex()

        etId.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val idInput = s.toString()
                val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$".toRegex()

                when {
                    idInput.isBlank() -> {
                        tvStatus.text = "아이디를 입력해주세요."
                        tvStatus.setTextColor(ContextCompat.getColor(requireContext(), R.color.red))
                    }
                    !emailRegex.matches(idInput) -> {
                        tvStatus.text = "이메일 형식이 아닙니다. 다시 시도해주세요."
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
            val input = etId.text.toString()
            val statusText = tvStatus.text.toString()

            if (statusText == "정상적으로 확인되었습니다 :)") {
                parentFragmentManager.beginTransaction()
                    .replace(R.id.signup_container, SignupStep4Fragment())
                    .addToBackStack(null)
                    .commit()
            }
        }


        btnBack.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.signup_container, SignupStep2Fragment())
                .addToBackStack(null)
                .commit()
        }

        return view
    }
}
