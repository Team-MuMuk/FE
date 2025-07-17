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

        binding.etName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val nickname = s.toString()
                when {
                    nickname.isBlank() -> {
                        binding.tvNameStatus.text = "이름을 입력해주세요."
                        binding.tvNameStatus.setTextColor(ContextCompat.getColor(requireContext(), R.color.red))
                        binding.ivNameStatusIcon.setImageResource(R.drawable.ic_error)
                        binding.ivNameStatusIcon.visibility = View.VISIBLE
                    }

                    nickname.length >= 10 -> {
                        binding.tvNameStatus.text = "글자 수가 초과되었습니다. 10자 이내로 입력해주세요."
                        binding.tvNameStatus.setTextColor(ContextCompat.getColor(requireContext(), R.color.red))
                        binding.ivNameStatusIcon.setImageResource(R.drawable.ic_error)
                        binding.ivNameStatusIcon.visibility = View.VISIBLE
                    }

                    else -> {
                        binding.tvNameStatus.text = "정상적으로 확인되었습니다"
                        binding.tvNameStatus.setTextColor(Color.parseColor("#306AF2"))
                        binding.ivNameStatusIcon.setImageResource(R.drawable.ic_check)
                        binding.ivNameStatusIcon.visibility = View.VISIBLE
                    }
                }
            }
        })


        binding.btnNext.setOnClickListener {
            val nickname = binding.etName.text.toString()
            if (nickname.isNotBlank() && nickname.length < 10) {
                (requireActivity() as SignupActivity).name = nickname
                parentFragmentManager.beginTransaction()
                    .replace(R.id.signup_container, SignupStep2Fragment())
                    .addToBackStack(null)
                    .commit()
            }
        }

        binding.btnBack.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.signup_container, SignupStep0Fragment())
                .addToBackStack(null)
                .commit()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
