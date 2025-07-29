package com.example.mumuk.ui.health

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.mumuk.databinding.FragmentHealthEditBinding
import com.example.mumuk.R
import com.google.android.material.button.MaterialButton

class HealthEditFragment : Fragment() {
    private var _binding: FragmentHealthEditBinding? = null
    private val binding get() = _binding!!

    // 카드 활성 상태 저장
    private var isAllergyEditSelected = false
    private var isGoalEditSelected = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHealthEditBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.backBtn.setOnClickListener {
            findNavController().popBackStack()
        }

        // 알레르기 정보 수정 카드 클릭 이벤트
        binding.cardViewAllergyEdit.setOnClickListener {
            isAllergyEditSelected = !isAllergyEditSelected
            toggleCard(
                selected = isAllergyEditSelected,
                card = binding.cardViewAllergyEdit,
                layout = binding.layoutAllergyEdit,
                textView = binding.tvAllergyGoal,
                imageView = binding.ivAllergyGoal,
                defaultText = "수정"
            )
        }

        // 건강 목표 수정 카드 클릭 이벤트
        binding.cardViewGoalEdit.setOnClickListener {
            isGoalEditSelected = !isGoalEditSelected
            toggleCard(
                selected = isGoalEditSelected,
                card = binding.cardViewGoalEdit,
                layout = binding.layoutGoalEdit,
                textView = binding.tvGoalEdit,
                imageView = binding.ivGoalEdit,
                defaultText = "수정"
            )
        }

        // 알레르기 버튼들 (다중 선택)
        val allergyButtons = listOf(
            binding.btnShellfish, binding.btnNuts, binding.btnDairy, binding.btnWheat,
            binding.btnEgg, binding.btnFish, binding.btnSoy, binding.btnNone
        )
        allergyButtons.forEach { btn ->
            btn.setOnClickListener {
                // 카드가 활성화(true)일 때만 버튼 토글
                if (!isAllergyEditSelected) {
                    btn.isChecked = !btn.isChecked
                }
                updateAllergyButtonColor(btn)
            }
            updateAllergyButtonColor(btn)
        }

        // 건강 목표 버튼들 (다중 선택)
        val goalButtons = listOf(
            binding.btnWeightLoss, binding.btnMuscleGain, binding.btnSugarReduction,
            binding.btnBloodPressure, binding.btnCholesterol, binding.btnDigestiveHealth, binding.btnNoneGoal
        )
        goalButtons.forEach { btn ->
            btn.setOnClickListener {
                if (isGoalEditSelected) {
                    btn.isChecked = !btn.isChecked
                }
                updateGoalButtonColor(btn)
            }
            updateGoalButtonColor(btn)
        }
    }

    // 카드 토글 함수
    private fun toggleCard(
        selected: Boolean,
        card: com.google.android.material.card.MaterialCardView,
        layout: View,
        textView: android.widget.TextView,
        imageView: android.widget.ImageView,
        defaultText: String
    ) {
        val green800 = ContextCompat.getColor(requireContext(), R.color.green_800)
        val white = ContextCompat.getColor(requireContext(), R.color.white)
        val grayStroke = ContextCompat.getColor(requireContext(), R.color.black_300)
        val defaultBackground = ContextCompat.getColor(requireContext(), R.color.white)
        val black = ContextCompat.getColor(requireContext(), R.color.black)

        if (selected) {
            card.strokeColor = green800
            layout.setBackgroundColor(green800)
            textView.setTextColor(white)
            imageView.setColorFilter(white)
            textView.text = "완료"
        } else {
            card.strokeColor = grayStroke
            layout.setBackgroundColor(defaultBackground)
            textView.setTextColor(black)
            imageView.setColorFilter(black)
            textView.text = defaultText
        }
    }

    // 알레르기 버튼 색상 업데이트 (다중 선택)
    private fun updateAllergyButtonColor(btn: MaterialButton) {
        if (!isAllergyEditSelected) { // 카드가 비활성화면 모두 기본색
            btn.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.health_button_background_selector))
            btn.setTextColor(ContextCompat.getColor(requireContext(), R.color.health_button_text_selector))
        } else {
            if (btn.isChecked) {
                btn.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.beige_500))
                btn.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            } else {
                btn.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.health_button_background_selector))
                btn.setTextColor(ContextCompat.getColor(requireContext(), R.color.health_button_text_selector))
            }
        }
    }

    // 건강목표 버튼 색상 업데이트 (다중 선택)
    private fun updateGoalButtonColor(btn: MaterialButton) {
        if (!isGoalEditSelected) {
            btn.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.health_button_background_selector))
            btn.setTextColor(ContextCompat.getColor(requireContext(), R.color.health_button_text_selector))
        } else {
            if (btn.isChecked) {
                btn.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.beige_500))
                btn.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            } else {
                btn.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.health_button_background_selector))
                btn.setTextColor(ContextCompat.getColor(requireContext(), R.color.health_button_text_selector))
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}