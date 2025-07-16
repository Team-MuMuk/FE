package com.example.mumuk.ui.signup

import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.mumuk.R
import com.example.mumuk.data.model.SignUpRequest
import com.example.mumuk.databinding.FragmentSignupStep6Binding
import kotlinx.coroutines.launch

class SignupStep6Fragment : Fragment() {

    private var _binding: FragmentSignupStep6Binding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignupStep6Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val activity = requireActivity() as SignupActivity
        val originalPassword = activity.password

        binding.etPw.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val input = s.toString()

                when {
                    input.isEmpty() -> {
                        binding.ivPwStatusIcon.setImageResource(R.drawable.ic_error)
                        binding.ivPwStatusIcon.visibility = View.VISIBLE
                        binding.tvPwStatus.text = "비밀번호를 입력해주세요."
                        binding.tvPwStatus.setTextColor(ContextCompat.getColor(requireContext(), R.color.red))
                        binding.layoutPw.error = null
                    }
                    input != originalPassword -> {
                        binding.ivPwStatusIcon.setImageResource(R.drawable.ic_error)
                        binding.ivPwStatusIcon.visibility = View.VISIBLE
                        binding.tvPwStatus.text = "비밀번호가 일치하지 않습니다."
                        binding.tvPwStatus.setTextColor(ContextCompat.getColor(requireContext(), R.color.red))
                        binding.layoutPw.error = null
                    }
                    else -> {
                        binding.ivPwStatusIcon.setImageResource(R.drawable.ic_check)
                        binding.ivPwStatusIcon.visibility = View.VISIBLE
                        binding.tvPwStatus.text = "비밀번호가 일치합니다"
                        binding.tvPwStatus.setTextColor(Color.parseColor("#306AF2"))
                        binding.layoutPw.error = null
                    }
                }
            }
        })

        binding.btnNext.setOnClickListener {
            val confirmPw = binding.etPw.text.toString()
            val statusText = binding.tvPwStatus.text.toString()

            if (statusText == "비밀번호가 일치합니다") {
                activity.confirmPassword = confirmPw

                val request = SignUpRequest(
                    name = activity.name,
                    nickname = activity.nickname,
                    phoneNumber = activity.phoneNumber,
                    loginId = activity.loginId,
                    password = activity.password,
                    confirmPassword = activity.confirmPassword
                )
                parentFragmentManager.beginTransaction()
                    .replace(R.id.signup_container, SignupCompleteFragment())
                    .addToBackStack(null)
                    .commit()
            }
        }

        binding.btnBack.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.signup_container, SignupStep5Fragment())
                .addToBackStack(null)
                .commit()
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
