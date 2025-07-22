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
import com.example.mumuk.databinding.FragmentSignupStep2Binding

class SignupStep2Fragment : Fragment() {

    private var _binding: FragmentSignupStep2Binding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignupStep2Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.etNickname.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val nickname = s.toString()
                when {
                    nickname.isBlank() -> {
                        binding.tvNicknameStatus.text = "닉네임을 입력해주세요."
                        binding.tvNicknameStatus.setTextColor(ContextCompat.getColor(requireContext(), R.color.red))
                        binding.ivNicknameStatusIcon.setImageResource(R.drawable.ic_error)
                        binding.ivNicknameStatusIcon.visibility = View.VISIBLE
                    }
                    nickname.length >= 10 -> {
                        binding.tvNicknameStatus.text = "글자 수가 초과되었습니다. 10자 이내로 입력해주세요."
                        binding.tvNicknameStatus.setTextColor(ContextCompat.getColor(requireContext(), R.color.red))
                        binding.ivNicknameStatusIcon.setImageResource(R.drawable.ic_error)
                        binding.ivNicknameStatusIcon.visibility = View.VISIBLE
                    }
                    else -> {
                        binding.tvNicknameStatus.text = "정상적으로 확인되었습니다"
                        binding.tvNicknameStatus.setTextColor(Color.parseColor("#306AF2"))
                        binding.ivNicknameStatusIcon.setImageResource(R.drawable.ic_check)
                        binding.ivNicknameStatusIcon.visibility = View.VISIBLE
                    }
                }
            }
        })

        binding.btnNext.setOnClickListener {
            val nickname = binding.etNickname.text.toString()
            if (nickname.isNotBlank() && nickname.length < 10) {
                (requireActivity() as SignupActivity).nickname = nickname
                parentFragmentManager.beginTransaction()
                    .replace(R.id.signup_container, SignupStep3Fragment())
                    .addToBackStack(null)
                    .commit()
            }
        }

        binding.btnBack.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.signup_container, SignupStep1Fragment())
                .addToBackStack(null)
                .commit()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
