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
import androidx.navigation.fragment.findNavController
import com.example.mumuk.R
import com.example.mumuk.databinding.FragmentSignupStep1Binding

class SignupStep1Fragment : Fragment() {

    private var _binding: FragmentSignupStep1Binding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignupStep1Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnNext.setImageResource(R.drawable.btn_next_gray)


        binding.etName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val nickname = s.toString()

                if (nickname.isBlank() || nickname.length >= 10) {
                    binding.btnNext.isEnabled = true
                    binding.btnNext.setImageResource(R.drawable.btn_next_gray)
                } else {
                    binding.btnNext.isEnabled = true
                    binding.btnNext.setImageResource(R.drawable.btn_next)
                }

                updateNameStatus(nickname)
            }
        })




        binding.btnNext.setOnClickListener {
            val nickname = binding.etName.text.toString()

            if (nickname.isNotBlank() && nickname.length < 10) {
                (requireActivity() as SignupActivity).name = nickname
                findNavController().navigate(R.id.action_step1_to_step2)

            } else {
                updateNameStatus(nickname)
            }
        }

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()

        }
    }

    private fun updateNameStatus(nickname: String) {
        when {
            nickname.isBlank() -> {
                binding.tvNameStatus.text = "이름을 입력해주세요."
                binding.tvNameStatus.setTextColor(ContextCompat.getColor(requireContext(), R.color.red))
                binding.ivNameStatusIcon.setImageResource(R.drawable.ic_error)
                binding.ivNameStatusIcon.visibility = View.VISIBLE
                binding.btnNext.isEnabled = false
            }

            nickname.length >= 10 -> {
                binding.tvNameStatus.text = "글자 수가 초과되었습니다. 10자 이내로 입력해주세요."
                binding.tvNameStatus.setTextColor(ContextCompat.getColor(requireContext(), R.color.red))
                binding.ivNameStatusIcon.setImageResource(R.drawable.ic_error)
                binding.ivNameStatusIcon.visibility = View.VISIBLE
                binding.btnNext.isEnabled = false
            }

            else -> {
                binding.tvNameStatus.text = "정상적으로 확인되었습니다"
                binding.tvNameStatus.setTextColor(Color.parseColor("#306AF2"))
                binding.ivNameStatusIcon.setImageResource(R.drawable.ic_check)
                binding.ivNameStatusIcon.visibility = View.VISIBLE
                binding.btnNext.isEnabled = true
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
