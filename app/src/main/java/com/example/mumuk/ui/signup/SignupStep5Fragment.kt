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
import com.example.mumuk.databinding.FragmentSignupStep5Binding

class SignupStep5Fragment : Fragment() {

    private var _binding: FragmentSignupStep5Binding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignupStep5Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.etPw.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val pw = s.toString()

                val hasLetter = pw.any { it.isLetter() }
                val hasDigit = pw.any { it.isDigit() }
                val hasSpecial = pw.any { !it.isLetterOrDigit() }
                val lengthValid = pw.length in 8..15

                val allValid = hasLetter && hasDigit && hasSpecial && lengthValid

                val containers = listOf(
                    binding.pwErrorContainer1,
                    binding.pwErrorContainer2,
                    binding.pwErrorContainer3,
                    binding.pwErrorContainer4
                )
                val icons = listOf(
                    binding.ivPwErrorIcon1,
                    binding.ivPwErrorIcon2,
                    binding.ivPwErrorIcon3,
                    binding.ivPwErrorIcon4
                )
                val messages = listOf(
                    binding.tvPwErrorMsg1,
                    binding.tvPwErrorMsg2,
                    binding.tvPwErrorMsg3,
                    binding.tvPwErrorMsg4
                )
                val messageList = mutableListOf<Pair<Int, String>>()

                messageList.add(
                    if (hasLetter)
                        Pair(R.drawable.ic_check, "영문자 사용")
                    else
                        Pair(R.drawable.ic_error, "영문자를 포함시켜주세요")
                )
                messageList.add(
                    if (hasDigit)
                        Pair(R.drawable.ic_check, "숫자 사용")
                    else
                        Pair(R.drawable.ic_error, "숫자를 포함시켜주세요")
                )
                messageList.add(
                    if (hasSpecial)
                        Pair(R.drawable.ic_check, "특수문자 사용")
                    else
                        Pair(R.drawable.ic_error, "특수문자를 포함하여 입력해주세요")
                )
                messageList.add(
                    if (lengthValid)
                        Pair(R.drawable.ic_check, "글자수 충족")
                    else
                        Pair(R.drawable.ic_error, "8자 이상 15자 이내로 입력해주세요")
                )

                containers.forEach { it.visibility = View.GONE }
                binding.pwSuccessContainer.visibility = View.GONE

                if (allValid) {
                    binding.pwErrorContainer1.visibility = View.VISIBLE
                    binding.ivPwErrorIcon1.setImageResource(R.drawable.ic_check)
                    binding.tvPwErrorMsg1.text = "정상적으로 확인되었습니다"
                    binding.tvPwErrorMsg1.setTextColor(Color.parseColor("#306AF2"))
                } else {
                    messageList.forEachIndexed { index, pair ->
                        containers[index].visibility = View.VISIBLE
                        icons[index].setImageResource(pair.first)
                        messages[index].text = pair.second
                        messages[index].setTextColor(
                            if (pair.first == R.drawable.ic_error)
                                ContextCompat.getColor(requireContext(), R.color.red)
                            else
                                Color.parseColor("#306AF2")
                        )
                    }
                }
            }
        })

        binding.btnNext.setOnClickListener {
            val input = binding.etPw.text.toString()
            (requireActivity() as SignupActivity).password = input
            val allConditionsMet = binding.tvPwErrorMsg1.text.toString() == "정상적으로 확인되었습니다"

            if (allConditionsMet) {
                val step5Fragment = SignupStep5Fragment()
                val bundle = Bundle().apply {
                    putString("password", input)
                }
                step5Fragment.arguments = bundle

                parentFragmentManager.beginTransaction()
                    .replace(R.id.signup_container, SignupStep6Fragment())
                    .addToBackStack(null)
                    .commit()
            }
        }

        binding.btnBack.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.signup_container, SignupStep4Fragment())
                .addToBackStack(null)
                .commit()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
