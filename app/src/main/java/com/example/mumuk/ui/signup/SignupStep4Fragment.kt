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
import com.example.mumuk.data.api.RetrofitClient
import com.example.mumuk.data.model.auth.CommonResponse
import com.example.mumuk.databinding.FragmentSignupStep4Binding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignupStep4Fragment : Fragment() {
    private var _binding: FragmentSignupStep4Binding? = null
    private val binding get() = _binding!!

    private var isLoginIdUnique = false

    private val handler = android.os.Handler(android.os.Looper.getMainLooper())
    private var debounceRunnable: Runnable? = null
    private var ongoingCall: Call<CommonResponse>? = null
    private var lastRequestedId: String? = null

    private var inFinalStatus = false        // 최종(사용가능/중복/오류) 메시지 모드
    private var lastFinalId: String? = null  // 마지막으로 최종 메시지 표시했던 문자열
    private var stickySuccess = false        // ‘사용 가능’ 상태 고정 여부

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignupStep4Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnNext.setImageResource(R.drawable.btn_next_gray)

        resetConditionViews()

        binding.etId.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                val idInput = s.toString()

                val hasLetter = idInput.any { it.isLetter() }
                val hasDigit  = idInput.any { it.isDigit() }
                val lengthOk  = idInput.length in 8..15
                val formatOk  = hasLetter && hasDigit && lengthOk

                if (stickySuccess) {
                    if (idInput.length > 15) {
                        stickySuccess = false
                        inFinalStatus = true
                        isLoginIdUnique = false
                        lastFinalId = idInput
                        showFinalStatus(false, "글자 수가 초과되었습니다.")
                        binding.btnNext.isEnabled = false
                        debounceRunnable?.let { handler.removeCallbacks(it) }
                        ongoingCall?.cancel()
                        return
                    }

                    if (!formatOk) {
                        stickySuccess = false
                        inFinalStatus = false
                        isLoginIdUnique = false
                        debounceRunnable?.let { handler.removeCallbacks(it) }
                        ongoingCall?.cancel()

                        showCondition(1, hasLetter, "영문자 포함", "영문자를 포함해주세요.")
                        showCondition(2, hasDigit,  "숫자 포함",   "숫자를 포함해주세요.")
                        showCondition(
                            3, lengthOk, "8~15자 입력",
                            if (idInput.length < 8) "글자 수가 미달되었습니다." else "글자 수가 초과되었습니다."
                        )
                        binding.btnNext.isEnabled = false
                        return
                    }

                    inFinalStatus = true
                    lastFinalId = idInput
                    showFinalStatus(true, "사용 가능한 아이디입니다")
                    binding.btnNext.isEnabled = formatOk && isLoginIdUnique

                    debounceRunnable?.let { handler.removeCallbacks(it) }
                    ongoingCall?.cancel()
                    debounceRunnable = Runnable { checkLoginIdDuplicate(idInput) }
                    handler.postDelayed(debounceRunnable!!, 300)
                    return
                }

                inFinalStatus = false
                isLoginIdUnique = false
                debounceRunnable?.let { handler.removeCallbacks(it) }
                ongoingCall?.cancel()

                if (idInput.isBlank()) {
                    showFinalStatus(false, "아이디를 입력해주세요.")
                    binding.btnNext.setImageResource(R.drawable.btn_next_gray)
                    return
                }

                if (!formatOk) {
                    renderConditionsDynamic(
                        hasLetter = hasLetter,
                        hasDigit = hasDigit,
                        lengthOk = lengthOk,
                        lengthFailText = if (idInput.length < 8) "글자 수가 미달되었습니다." else "글자 수가 초과되었습니다."
                    )
                    binding.btnNext.setImageResource(R.drawable.btn_next_gray)
                    return
                }

                hideConditions()
                binding.btnNext.isEnabled = false
                debounceRunnable = Runnable { checkLoginIdDuplicate(idInput) }
                handler.postDelayed(debounceRunnable!!, 300)

            }
        })

        binding.btnNext.setOnClickListener {
            val idInput   = binding.etId.text.toString()
            val hasLetter = idInput.any { it.isLetter() }
            val hasDigit  = idInput.any { it.isDigit() }
            val lengthOk  = idInput.length in 8..15
            val formatOk  = hasLetter && hasDigit && lengthOk

            if (idInput.isBlank()) {
                showFinalStatus(false, "아이디를 입력해주세요.")
                return@setOnClickListener
            }
            if (!formatOk) return@setOnClickListener

            if (isLoginIdUnique) {
                (requireActivity() as SignupActivity).loginId = idInput
                findNavController().navigate(R.id.action_step4_to_step5)

            }
        }

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()

        }
    }

    private fun checkLoginIdDuplicate(loginId: String) {
        ongoingCall?.cancel()
        lastRequestedId = loginId

        val call = RetrofitClient.getAuthApi(requireContext()).checkLoginIdExists(loginId)
        ongoingCall = call

        call.enqueue(object : Callback<CommonResponse> {
            override fun onResponse(call: Call<CommonResponse>, response: Response<CommonResponse>) {
                if (call.isCanceled) return
                val current = binding.etId.text?.toString() ?: ""
                if (current != lastRequestedId) return

                val message = response.body()?.data?.toString() ?: ""

                if (response.isSuccessful) {
                    when {
                        message.contains("사용 가능") -> {
                            isLoginIdUnique = true
                            inFinalStatus = true
                            stickySuccess = true
                            lastFinalId = current
                            showFinalStatus(true, "사용 가능한 아이디입니다")

                            val hasLetter = current.any { it.isLetter() }
                            val hasDigit  = current.any { it.isDigit() }
                            val lengthOk  = current.length in 8..15
                            binding.btnNext.isEnabled = hasLetter && hasDigit && lengthOk && isLoginIdUnique
                            binding.btnNext.setImageResource(R.drawable.btn_next)
                        }
                        message.contains("이미 사용") -> {
                            isLoginIdUnique = false
                            inFinalStatus = true
                            stickySuccess = false
                            lastFinalId = current
                            showFinalStatus(false, "중복된 아이디입니다")
                            binding.btnNext.setImageResource(R.drawable.btn_next_gray)
                        }
                        else -> {
                            isLoginIdUnique = false
                            inFinalStatus = true
                            lastFinalId = current
                            showFinalStatus(false, "응답을 이해할 수 없습니다")
                            binding.btnNext.isEnabled = false
                        }
                    }
                } else {
                    isLoginIdUnique = false
                    inFinalStatus = true
                    lastFinalId = current
                    showFinalStatus(false, "서버 오류가 발생했습니다")
                    binding.btnNext.isEnabled = false
                }
            }

            override fun onFailure(call: Call<CommonResponse>, t: Throwable) {
                if (call.isCanceled) return
                val current = binding.etId.text?.toString() ?: ""
                if (current != lastRequestedId) return

                if (!stickySuccess) {
                    isLoginIdUnique = false
                    inFinalStatus = true
                    showFinalStatus(false, "네트워크 오류가 발생했습니다")
                    binding.btnNext.isEnabled = false
                }
            }
        })
    }

    private fun resetConditionViews() {
        binding.idErrorContainer1.visibility = View.GONE
        binding.idErrorContainer2.visibility = View.GONE
        binding.idErrorContainer3.visibility = View.GONE
    }

    private fun hideConditions() {
        binding.idErrorContainer1.visibility = View.GONE
        binding.idErrorContainer2.visibility = View.GONE
        binding.idErrorContainer3.visibility = View.GONE
    }

    private fun showFinalStatus(ok: Boolean, message: String) {
        binding.idErrorContainer1.visibility = View.VISIBLE
        binding.idErrorContainer2.visibility = View.GONE
        binding.idErrorContainer3.visibility = View.GONE

        val iconRes = if (ok) R.drawable.ic_check else R.drawable.ic_error
        val color = if (ok) Color.parseColor("#306AF2")
        else ContextCompat.getColor(requireContext(), R.color.red)

        if (binding.ivIdErrorIcon1.tag != iconRes) {
            binding.ivIdErrorIcon1.setImageResource(iconRes)
            binding.ivIdErrorIcon1.tag = iconRes
        }
        if (binding.tvIdErrorMsg1.text.toString() != message) {
            binding.tvIdErrorMsg1.text = message
        }
        if (binding.tvIdErrorMsg1.currentTextColor != color) {
            binding.tvIdErrorMsg1.setTextColor(color)
        }
    }

    private fun showCondition(containerIndex: Int, ok: Boolean, okText: String, failText: String) {
        if (inFinalStatus) return

        val iconRes = if (ok) R.drawable.ic_check else R.drawable.ic_error
        val color = if (ok) Color.parseColor("#306AF2")
        else ContextCompat.getColor(requireContext(), R.color.red)
        val text = if (ok) okText else failText

        when (containerIndex) {
            1 -> {
                binding.idErrorContainer1.visibility = View.VISIBLE
                if (binding.ivIdErrorIcon1.tag != iconRes) {
                    binding.ivIdErrorIcon1.setImageResource(iconRes)
                    binding.ivIdErrorIcon1.tag = iconRes
                }
                if (binding.tvIdErrorMsg1.text.toString() != text) {
                    binding.tvIdErrorMsg1.text = text
                }
                if (binding.tvIdErrorMsg1.currentTextColor != color) {
                    binding.tvIdErrorMsg1.setTextColor(color)
                }
            }
            2 -> {
                binding.idErrorContainer2.visibility = View.VISIBLE
                if (binding.ivIdErrorIcon2.tag != iconRes) {
                    binding.ivIdErrorIcon2.setImageResource(iconRes)
                    binding.ivIdErrorIcon2.tag = iconRes
                }
                if (binding.tvIdErrorMsg2.text.toString() != text) {
                    binding.tvIdErrorMsg2.text = text
                }
                if (binding.tvIdErrorMsg2.currentTextColor != color) {
                    binding.tvIdErrorMsg2.setTextColor(color)
                }
            }
            3 -> {
                binding.idErrorContainer3.visibility = View.VISIBLE
                if (binding.ivIdErrorIcon3.tag != iconRes) {
                    binding.ivIdErrorIcon3.setImageResource(iconRes)
                    binding.ivIdErrorIcon3.tag = iconRes
                }
                if (binding.tvIdErrorMsg3.text.toString() != text) {
                    binding.tvIdErrorMsg3.text = text
                }
                if (binding.tvIdErrorMsg3.currentTextColor != color) {
                    binding.tvIdErrorMsg3.setTextColor(color)
                }
            }
        }
    }

    // 조건 한 줄을 컨테이너에 채워 넣는 헬퍼
    private fun bindConditionRow(
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
                binding.idErrorContainer1.visibility = View.VISIBLE
                if (binding.ivIdErrorIcon1.tag != iconRes) {
                    binding.ivIdErrorIcon1.setImageResource(iconRes)
                    binding.ivIdErrorIcon1.tag = iconRes
                }
                if (binding.tvIdErrorMsg1.text.toString() != text) {
                    binding.tvIdErrorMsg1.text = text
                }
                if (binding.tvIdErrorMsg1.currentTextColor != color) {
                    binding.tvIdErrorMsg1.setTextColor(color)
                }
            }
            2 -> {
                binding.idErrorContainer2.visibility = View.VISIBLE
                if (binding.ivIdErrorIcon2.tag != iconRes) {
                    binding.ivIdErrorIcon2.setImageResource(iconRes)
                    binding.ivIdErrorIcon2.tag = iconRes
                }
                if (binding.tvIdErrorMsg2.text.toString() != text) {
                    binding.tvIdErrorMsg2.text = text
                }
                if (binding.tvIdErrorMsg2.currentTextColor != color) {
                    binding.tvIdErrorMsg2.setTextColor(color)
                }
            }
            3 -> {
                binding.idErrorContainer3.visibility = View.VISIBLE
                if (binding.ivIdErrorIcon3.tag != iconRes) {
                    binding.ivIdErrorIcon3.setImageResource(iconRes)
                    binding.ivIdErrorIcon3.tag = iconRes
                }
                if (binding.tvIdErrorMsg3.text.toString() != text) {
                    binding.tvIdErrorMsg3.text = text
                }
                if (binding.tvIdErrorMsg3.currentTextColor != color) {
                    binding.tvIdErrorMsg3.setTextColor(color)
                }
            }
        }
    }


    private fun renderConditionsDynamic(
        hasLetter: Boolean,
        hasDigit: Boolean,
        lengthOk: Boolean,
        lengthFailText: String // "글자 수가 미달되었습니다." 또는 "초과되었습니다."
    ) {
        if (inFinalStatus) return

        hideConditions()

        data class Cond(val ok: Boolean, val okText: String, val failText: String)

        val conds = listOf(
            Cond(hasLetter, "영문자 포함", "영문자를 포함해주세요."),
            Cond(hasDigit,  "숫자 포함",   "숫자를 포함해주세요."),
            Cond(lengthOk,  "8~15자 입력", lengthFailText)
        )

        val ordered = conds.sortedByDescending { it.ok }

        ordered.forEachIndexed { idx, c ->
            val containerIndex = idx + 1
            bindConditionRow(containerIndex, c.ok, c.okText, c.failText)
        }
    }


    override fun onDestroyView() {
        debounceRunnable?.let { handler.removeCallbacks(it) }
        ongoingCall?.cancel()
        _binding = null
        super.onDestroyView()
    }
}
