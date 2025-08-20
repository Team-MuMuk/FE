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
import androidx.navigation.fragment.findNavController
import com.example.mumuk.R
import com.example.mumuk.data.api.RetrofitClient
import com.example.mumuk.data.model.auth.CommonResponse
import com.example.mumuk.databinding.FragmentSignupStep3Binding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignupStep3Fragment : Fragment() {

    private var _binding: FragmentSignupStep3Binding? = null
    private val binding get() = _binding!!

    private var isPhoneNumberUnique = false

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

        binding.btnNext.isEnabled = true
        binding.btnNext.setImageResource(R.drawable.btn_next_gray)

        binding.etNumber.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                val number = s.toString()
                val isAllDigits = number.all { it.isDigit() }
                val isLengthValid = number.length == 11

                isPhoneNumberUnique = false

                when {
                    number.isEmpty() -> {
                        binding.numberConditionContainer1.visibility = View.VISIBLE
                        binding.ivNumberConditionIcon1.setImageResource(R.drawable.ic_error)
                        binding.tvNumberConditionMsg1.text = "전화번호를 입력하세요"
                        binding.tvNumberConditionMsg1.setTextColor(
                            ContextCompat.getColor(requireContext(), R.color.red)
                        )
                        binding.numberConditionContainer2.visibility = View.GONE
                    }
                    isAllDigits && isLengthValid -> {
                        binding.numberConditionContainer2.visibility = View.GONE
                        checkPhoneNumberDuplicate(number)
                    }
                    else -> {
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
                    }
                }

                updateNextButtonColor(isAllDigits, isLengthValid, isPhoneNumberUnique)
            }
        })

        binding.etNumber.setOnKeyListener { _, _, event ->
            if (event.action == KeyEvent.ACTION_DOWN) {
                val capsOn = event.metaState and KeyEvent.META_CAPS_LOCK_ON != 0
                val numOn = event.metaState and KeyEvent.META_NUM_LOCK_ON != 0

            }
            false
        }

        binding.btnNext.setOnClickListener {
            val number = binding.etNumber.text.toString()
            val isAllDigits = number.all { it.isDigit() }
            val isLengthValid = number.length == 11

            if (number.isEmpty()) {
                binding.numberConditionContainer1.visibility = View.VISIBLE
                binding.ivNumberConditionIcon1.setImageResource(R.drawable.ic_error)
                binding.tvNumberConditionMsg1.text = "전화번호를 입력하세요"
                binding.tvNumberConditionMsg1.setTextColor(
                    ContextCompat.getColor(requireContext(), R.color.red)
                )
                updateNextButtonColor(isAllDigits, isLengthValid, isPhoneNumberUnique)
                return@setOnClickListener
            }

            if (isAllDigits && isLengthValid && isPhoneNumberUnique) {
                (requireActivity() as SignupActivity).phoneNumber = number
                findNavController().navigate(R.id.action_step3_to_step4)

            } else {
                updateNextButtonColor(isAllDigits, isLengthValid, isPhoneNumberUnique)
            }
        }

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()

        }
    }

    private fun checkPhoneNumberDuplicate(phoneNumber: String) {
        RetrofitClient.getAuthApi(requireContext()).checkPhoneNumberExists(phoneNumber)
            .enqueue(object : Callback<CommonResponse> {
                override fun onResponse(
                    call: Call<CommonResponse>,
                    response: Response<CommonResponse>
                ) {
                    val message = response.body()?.data?.toString() ?: ""

                    if (response.isSuccessful) {
                        when {
                            message.contains("사용 가능") -> {
                                isPhoneNumberUnique = true
                                binding.numberConditionContainer1.visibility = View.VISIBLE
                                binding.ivNumberConditionIcon1.setImageResource(R.drawable.ic_check)
                                binding.tvNumberConditionMsg1.text = "정상적으로 확인되었습니다"
                                binding.tvNumberConditionMsg1.setTextColor(Color.parseColor("#306AF2"))
                            }
                            message.contains("이미 사용") -> {
                                isPhoneNumberUnique = false
                                binding.numberConditionContainer1.visibility = View.VISIBLE
                                binding.ivNumberConditionIcon1.setImageResource(R.drawable.ic_error)
                                binding.tvNumberConditionMsg1.text = "중복된 전화번호입니다"
                                binding.tvNumberConditionMsg1.setTextColor(
                                    ContextCompat.getColor(requireContext(), R.color.red)
                                )
                            }
                            else -> {
                                isPhoneNumberUnique = false
                                binding.numberConditionContainer1.visibility = View.VISIBLE
                                binding.ivNumberConditionIcon1.setImageResource(R.drawable.ic_error)
                                binding.tvNumberConditionMsg1.text = "응답을 이해할 수 없습니다"
                                binding.tvNumberConditionMsg1.setTextColor(
                                    ContextCompat.getColor(requireContext(), R.color.red)
                                )
                            }
                        }
                    } else {
                        isPhoneNumberUnique = false
                        binding.numberConditionContainer1.visibility = View.VISIBLE
                        binding.ivNumberConditionIcon1.setImageResource(R.drawable.ic_error)
                        binding.tvNumberConditionMsg1.text = "서버 오류가 발생했습니다"
                        binding.tvNumberConditionMsg1.setTextColor(
                            ContextCompat.getColor(requireContext(), R.color.red)
                        )
                    }

                    val number = binding.etNumber.text.toString()
                    val isAllDigits = number.all { it.isDigit() }
                    val isLengthValid = number.length == 11
                    updateNextButtonColor(isAllDigits, isLengthValid, isPhoneNumberUnique)
                }

                override fun onFailure(call: Call<CommonResponse>, t: Throwable) {
                    isPhoneNumberUnique = false
                    binding.numberConditionContainer1.visibility = View.VISIBLE
                    binding.ivNumberConditionIcon1.setImageResource(R.drawable.ic_error)
                    binding.tvNumberConditionMsg1.text = "네트워크 오류가 발생했습니다"
                    binding.tvNumberConditionMsg1.setTextColor(
                        ContextCompat.getColor(requireContext(), R.color.red)
                    )

                    val number = binding.etNumber.text.toString()
                    val isAllDigits = number.all { it.isDigit() }
                    val isLengthValid = number.length == 11
                    updateNextButtonColor(isAllDigits, isLengthValid, isPhoneNumberUnique)
                }
            })
    }

    private fun updateNextButtonColor(
        isAllDigits: Boolean,
        isLengthValid: Boolean,
        isUnique: Boolean
    ) {
        binding.btnNext.setImageResource(
            if (isAllDigits && isLengthValid && isUnique)
                R.drawable.btn_next
            else
                R.drawable.btn_next_gray
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
