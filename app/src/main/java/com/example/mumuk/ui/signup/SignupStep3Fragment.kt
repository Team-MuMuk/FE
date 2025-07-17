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
import com.example.mumuk.databinding.FragmentSignupStep3Binding

class SignupStep3Fragment : Fragment() {

    private var _binding: FragmentSignupStep3Binding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignupStep3Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 전화번호 검사
        binding.etNumber.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                val number = s.toString()
                when {
                    number.isBlank() -> {
                        binding.tvNumberStatus.text = "전화번호를 입력해주세요."
                        binding.tvNumberStatus.setTextColor(ContextCompat.getColor(requireContext(), R.color.red))
                        binding.ivNumberStatusIcon.setImageResource(R.drawable.ic_error)
                        binding.ivNumberStatusIcon.visibility = View.VISIBLE
                    }
                    number.length != 11 || !number.all { it.isDigit() } -> {
                        binding.tvNumberStatus.text = "전화번호 11자리를 입력해주세요."
                        binding.tvNumberStatus.setTextColor(ContextCompat.getColor(requireContext(), R.color.red))
                        binding.ivNumberStatusIcon.setImageResource(R.drawable.ic_error)
                        binding.ivNumberStatusIcon.visibility = View.VISIBLE
                    }
                    else -> {
                        binding.tvNumberStatus.text = "정상적으로 확인되었습니다"
                        binding.tvNumberStatus.setTextColor(Color.parseColor("#306AF2"))
                        binding.ivNumberStatusIcon.setImageResource(R.drawable.ic_check)
                        binding.ivNumberStatusIcon.visibility = View.VISIBLE
                    }
                }
            }
        })


                binding.btnNext.setOnClickListener {
            val number = binding.etNumber.text.toString()

            val isNumberValid = number.length == 11 && number.all { it.isDigit() }

            if (isNumberValid) {
                val activity = requireActivity() as SignupActivity
                activity.phoneNumber = number

                parentFragmentManager.beginTransaction()
                    .replace(R.id.signup_container, SignupStep4Fragment())
                    .addToBackStack(null)
                    .commit()
            }
        }

                binding.btnBack.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.signup_container, SignupStep2Fragment())
                .addToBackStack(null)
                .commit()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
