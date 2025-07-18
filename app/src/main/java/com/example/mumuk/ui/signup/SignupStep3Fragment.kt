package com.example.mumuk.ui.signup

import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
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

        binding.etNumber.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                val number = s.toString()
                val isAllDigits = number.all { it.isDigit() }
                val isLengthValid = number.length == 11

                // 조건 1: 숫자만 입력
                binding.numberConditionContainer1.visibility = View.VISIBLE
                binding.ivNumberConditionIcon1.setImageResource(
                    if (isAllDigits) R.drawable.ic_check else R.drawable.ic_error
                )
                binding.tvNumberConditionMsg1.text =
                    if (isAllDigits) "숫자만 입력됨" else "숫자만 입력해주세요"
                binding.tvNumberConditionMsg1.setTextColor(
                    if (isAllDigits)
                        Color.parseColor("#306AF2")
                    else
                        ContextCompat.getColor(requireContext(), R.color.red)
                )

                // 조건 2: 11자리 입력
                binding.numberConditionContainer2.visibility = View.VISIBLE
                binding.ivNumberConditionIcon2.setImageResource(
                    if (isLengthValid) R.drawable.ic_check else R.drawable.ic_error
                )
                binding.tvNumberConditionMsg2.text =
                    if (isLengthValid) "11자리 입력" else "11자리를 입력해주세요"
                binding.tvNumberConditionMsg2.setTextColor(
                    if (isLengthValid)
                        Color.parseColor("#306AF2")
                    else
                        ContextCompat.getColor(requireContext(), R.color.red)
                )

                // 조건 충족 시 버튼 활성화 (선택)
                binding.btnNext.isEnabled = isAllDigits && isLengthValid
            }
        })

        binding.etNumber.setOnKeyListener { _, _, event ->
            if (event.action == KeyEvent.ACTION_DOWN) {
                val capsOn = event.metaState and KeyEvent.META_CAPS_LOCK_ON != 0
                val numOn = event.metaState and KeyEvent.META_NUM_LOCK_ON != 0

                binding.layoutCapsLockWarning.visibility = if (capsOn) View.VISIBLE else View.GONE
                binding.layoutNumLockWarning.visibility = if (numOn) View.VISIBLE else View.GONE
            }
            false
        }

        binding.btnNext.setOnClickListener {
            val number = binding.etNumber.text.toString()
            val isValid = number.length == 11 && number.all { it.isDigit() }

            if (isValid) {
                (requireActivity() as SignupActivity).phoneNumber = number

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
