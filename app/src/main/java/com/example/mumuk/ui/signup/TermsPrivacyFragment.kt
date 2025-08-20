package com.example.mumuk.ui.signup

import android.app.Dialog
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.mumuk.R
import com.example.mumuk.databinding.FragmentTermsPrivacyBinding

class TermsPrivacyFragment : Fragment() {

    private var _binding: FragmentTermsPrivacyBinding? = null
    private val binding get() = _binding!!

    private var isConfirmAgreeChecked = false
    private var isTermsAgreeChecked = false
    private var isDeleteAgreeChecked = false
    private var isSensitiveInfoChecked = false
    private var isAllAgreeChecked = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTermsPrivacyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        applySpannableTexts()

        binding.layoutTermsAgree.setOnClickListener {
            isTermsAgreeChecked = !isTermsAgreeChecked
            binding.ivCheckTermsAgree.setImageResource(
                if (isTermsAgreeChecked) R.drawable.ic_check_blue else R.drawable.ic_check_gray
            )
            syncAllAgreeCheck()
        }

        binding.layoutDeleteAgree.setOnClickListener {
            isDeleteAgreeChecked = !isDeleteAgreeChecked
            binding.ivCheckDeleteAgree.setImageResource(
                if (isDeleteAgreeChecked) R.drawable.ic_check_blue else R.drawable.ic_check_gray
            )
            syncAllAgreeCheck()
        }


        binding.layoutConfirmAgree.setOnClickListener {
            isConfirmAgreeChecked = !isConfirmAgreeChecked
            binding.ivCheckConfirmAgree.setImageResource(
                if (isConfirmAgreeChecked) R.drawable.ic_check_blue else R.drawable.ic_check_gray
            )
            syncAllAgreeCheck()
        }

        binding.layoutSensitiveInfo.setOnClickListener {
            isSensitiveInfoChecked = !isSensitiveInfoChecked
            binding.ivCheckSensitiveInfo.setImageResource(
                if (isSensitiveInfoChecked) R.drawable.ic_check_blue else R.drawable.ic_check_gray
            )
            syncAllAgreeCheck()
        }

        binding.layoutAllAgree.setOnClickListener {
            isAllAgreeChecked = !isAllAgreeChecked

            binding.ivCheckAllAgree.visibility = if (isAllAgreeChecked) View.VISIBLE else View.GONE

            isConfirmAgreeChecked = isAllAgreeChecked
            isSensitiveInfoChecked = isAllAgreeChecked
            isTermsAgreeChecked = isAllAgreeChecked
            isDeleteAgreeChecked = isAllAgreeChecked

            binding.ivCheckConfirmAgree.setImageResource(
                if (isConfirmAgreeChecked) R.drawable.ic_check_blue else R.drawable.ic_check_gray
            )
            binding.ivCheckSensitiveInfo.setImageResource(
                if (isSensitiveInfoChecked) R.drawable.ic_check_blue else R.drawable.ic_check_gray
            )
            binding.ivCheckTermsAgree.setImageResource(
                if (isTermsAgreeChecked) R.drawable.ic_check_blue else R.drawable.ic_check_gray
            )
            binding.ivCheckDeleteAgree.setImageResource(
                if (isDeleteAgreeChecked) R.drawable.ic_check_blue else R.drawable.ic_check_gray
            )

            updateAgreeButtonState()
        }


        binding.btnAgree.setOnClickListener {
            showSimpleConfirmDialog("동의하시겠습니까?", "확인") {
                showSimpleConfirmDialog("정상처리되었습니다.", "확인") {
                    findNavController().navigate(R.id.action_terms_to_complete)

                }
            }
        }

        binding.btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        updateAgreeButtonState()
    }

    private fun syncAllAgreeCheck() {
        val allChecked = isConfirmAgreeChecked && isSensitiveInfoChecked && isTermsAgreeChecked && isDeleteAgreeChecked
        isAllAgreeChecked = allChecked
        binding.ivCheckAllAgree.visibility = if (allChecked) View.VISIBLE else View.GONE
        updateAgreeButtonState()
    }



    private fun updateAgreeButtonState() {
        val enabled = isConfirmAgreeChecked && isSensitiveInfoChecked && isTermsAgreeChecked && isDeleteAgreeChecked
        val colorHex = if (enabled) "#29AB87" else "#CFCFCF"

        binding.btnAgree.isEnabled = enabled
        binding.btnAgree.backgroundTintList = ColorStateList.valueOf(Color.parseColor(colorHex))
    }


    private fun applySpannableTexts() {
        val confirmText = "위 내용을 모두 확인하였으며, 개인정보 수집 및 이용에 동의합니다. (필수)"
        val confirmSpannable = SpannableString(confirmText)
        val confirmStart = confirmText.indexOf("(필수)")
        confirmSpannable.setSpan(
            ForegroundColorSpan(Color.parseColor("#306AF2")),
            confirmStart,
            confirmStart + "(필수)".length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        binding.tvConfirmAgreeText.text = confirmSpannable

        val sensitiveText = "건강정보(민감정보)의 수집 및 분석, 히스토리 저장, 식단 추천 활용에 동의합니다. (필수)"
        val sensitiveSpannable = SpannableString(sensitiveText)
        val sensitiveStart = sensitiveText.indexOf("(필수)")
        sensitiveSpannable.setSpan(
            ForegroundColorSpan(Color.parseColor("#306AF2")),
            sensitiveStart,
            sensitiveStart + "(필수)".length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        binding.tvSensitiveInfoText.text = sensitiveSpannable
    }


    private fun showSimpleConfirmDialog(
        message: String,
        buttonText: String = "확인",
        onButtonClick: (() -> Unit)? = null
    ) {
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.dialog_confirm)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialog.window?.setDimAmount(0f)
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )

        val tvMessage = dialog.findViewById<TextView>(R.id.tv_dialog_message)
        val btnOk = dialog.findViewById<TextView>(R.id.btn_dialog_ok)

        tvMessage.text = message
        btnOk.text = buttonText

        btnOk.setOnClickListener {
            dialog.dismiss()
            onButtonClick?.invoke()
        }

        dialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
