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

        binding.btnNext.setImageResource(R.drawable.btn_next_gray)

        binding.etPw.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val pw = s.toString()

                if (pw.isBlank()) {
                    hidePwConditions()
                    binding.pwSuccessContainer.visibility = View.GONE

                    binding.pwErrorContainer1.visibility = View.VISIBLE
                    binding.ivPwErrorIcon1.setImageResource(R.drawable.ic_error)
                    binding.tvPwErrorMsg1.text = "비밀번호를 입력해주세요"
                    binding.tvPwErrorMsg1.setTextColor(
                        ContextCompat.getColor(requireContext(), R.color.red)
                    )

                    binding.btnNext.setImageResource(R.drawable.btn_next_gray)
                    return
                }

                val hasLetter = pw.any { it.isLetter() }
                val hasDigit = pw.any { it.isDigit() }
                val hasSpecial = pw.any { !it.isLetterOrDigit() }
                val lengthValid = pw.length in 8..15

                val allValid = hasLetter && hasDigit && hasSpecial && lengthValid

                hidePwConditions()
                binding.pwSuccessContainer.visibility = View.GONE

                if (allValid) {
                    binding.pwErrorContainer1.visibility = View.VISIBLE
                    binding.ivPwErrorIcon1.setImageResource(R.drawable.ic_check)
                    binding.tvPwErrorMsg1.text = "정상적으로 확인되었습니다"
                    binding.tvPwErrorMsg1.setTextColor(Color.parseColor("#306AF2"))

                    binding.btnNext.setImageResource(R.drawable.btn_next)
                } else {
                    renderPwConditionsDynamic(hasLetter, hasDigit, hasSpecial, lengthValid)
                    binding.btnNext.setImageResource(R.drawable.btn_next_gray)
                }
            }

        })

        binding.btnNext.setOnClickListener {
            val input = binding.etPw.text.toString()
            (requireActivity() as SignupActivity).password = input
            val allConditionsMet = binding.tvPwErrorMsg1.text.toString() == "정상적으로 확인되었습니다"

            if (input.isBlank()) {
                hidePwConditions()
                binding.pwSuccessContainer.visibility = View.GONE

                binding.pwErrorContainer1.visibility = View.VISIBLE
                binding.ivPwErrorIcon1.setImageResource(R.drawable.ic_error)
                binding.tvPwErrorMsg1.text = "비밀번호를 입력해주세요"
                binding.tvPwErrorMsg1.setTextColor(
                    ContextCompat.getColor(requireContext(), R.color.red)
                )

                binding.btnNext.setImageResource(R.drawable.btn_next_gray)
                binding.etPw.requestFocus()
                return@setOnClickListener
            }

            if (allConditionsMet) {
                val step5Fragment = SignupStep5Fragment()
                val bundle = Bundle().apply {
                    putString("password", input)
                }
                step5Fragment.arguments = bundle

                findNavController().navigate(R.id.action_step5_to_step6)

            }
        }

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()

        }
    }

    private fun hidePwConditions() {
        binding.pwErrorContainer1.visibility = View.GONE
        binding.pwErrorContainer2.visibility = View.GONE
        binding.pwErrorContainer3.visibility = View.GONE
        binding.pwErrorContainer4.visibility = View.GONE
    }

    private fun bindPwRow(
        containerIndex: Int,
        ok: Boolean,
        okText: String,
        failText: String
    ) {
        val iconRes = if (ok) R.drawable.ic_check else R.drawable.ic_error
        val color = if (ok) Color.parseColor("#306AF2")
        else ContextCompat.getColor(requireContext(), R.color.red)
        val text = if (ok) okText else failText

        when (containerIndex) {
            1 -> {
                binding.pwErrorContainer1.visibility = View.VISIBLE
                binding.ivPwErrorIcon1.setImageResource(iconRes)
                binding.tvPwErrorMsg1.text = text
                binding.tvPwErrorMsg1.setTextColor(color)
            }
            2 -> {
                binding.pwErrorContainer2.visibility = View.VISIBLE
                binding.ivPwErrorIcon2.setImageResource(iconRes)
                binding.tvPwErrorMsg2.text = text
                binding.tvPwErrorMsg2.setTextColor(color)
            }
            3 -> {
                binding.pwErrorContainer3.visibility = View.VISIBLE
                binding.ivPwErrorIcon3.setImageResource(iconRes)
                binding.tvPwErrorMsg3.text = text
                binding.tvPwErrorMsg3.setTextColor(color)
            }
            4 -> {
                binding.pwErrorContainer4.visibility = View.VISIBLE
                binding.ivPwErrorIcon4.setImageResource(iconRes)
                binding.tvPwErrorMsg4.text = text
                binding.tvPwErrorMsg4.setTextColor(color)
            }
        }
    }

    private fun renderPwConditionsDynamic(
        hasLetter: Boolean,
        hasDigit: Boolean,
        hasSpecial: Boolean,
        lengthValid: Boolean
    ) {
        hidePwConditions()

        data class Cond(val ok: Boolean, val okText: String, val failText: String)

        val conds = listOf(
            Cond(hasLetter, "영문자 사용", "영문자를 포함해주세요."),
            Cond(hasDigit,  "숫자 사용",   "숫자를 포함해주세요."),
            Cond(hasSpecial,"특수문자 사용", "특수문자를 포함해주세요."),
            Cond(lengthValid,"글자수 충족", "8자 이상 15자 이내로 입력해주세요.")
        )

        val ordered = conds.sortedByDescending { it.ok }   // true 먼저
        ordered.forEachIndexed { idx, c -> bindPwRow(idx + 1, c.ok, c.okText, c.failText) }
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
