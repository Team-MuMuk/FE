package com.example.mumuk.ui.signup

import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.mumuk.R
import com.example.mumuk.databinding.FragmentSignupStep4Binding

class SignupStep4Fragment : Fragment() {
    private var _binding: FragmentSignupStep4Binding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignupStep4Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.etId.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                val idInput = s.toString()

                // 모든 메시지 숨김
                binding.idErrorContainer1.visibility = View.GONE
                binding.idErrorContainer2.visibility = View.GONE
                binding.idSuccessContainer.visibility = View.GONE

                val errors = mutableListOf<String>()
                if (!idInput.any { it.isLetter() }) {
                    errors.add("영문자를 포함해주세요.")
                }
                if (!idInput.any { it.isDigit() }) {
                    errors.add("숫자를 포함해주세요.")
                }
                if (idInput.length < 8) {
                    errors.add("글자 수가 미달되었습니다.")
                } else if (idInput.length > 15) {
                    errors.add("글자 수가 초과되었습니다.")
                }

                when {
                    idInput.isBlank() -> {
                        binding.idErrorContainer1.visibility = View.VISIBLE
                        binding.tvIdErrorMsg1.text = "아이디를 입력해주세요."
                    }

                    errors.isNotEmpty() -> {
                        if (errors.size >= 1) {
                            binding.idErrorContainer1.visibility = View.VISIBLE
                            binding.tvIdErrorMsg1.text = errors[0]
                        }
                        if (errors.size >= 2) {
                            binding.idErrorContainer2.visibility = View.VISIBLE
                            binding.tvIdErrorMsg2.text = errors[1]
                        }
                    }

                    else -> {
                        binding.idSuccessContainer.visibility = View.VISIBLE
                    }
                }
            }
        })

        binding.btnNext.setOnClickListener {
            if (binding.idSuccessContainer.visibility == View.VISIBLE) {
                val loginId = binding.etId.text.toString()
                (requireActivity() as SignupActivity).loginId = loginId

                parentFragmentManager.beginTransaction()
                    .replace(R.id.signup_container, SignupStep5Fragment())
                    .addToBackStack(null)
                    .commit()
            }
        }


        binding.btnBack.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.signup_container, SignupStep3Fragment())
                .addToBackStack(null)
                .commit()
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
