package com.example.mumuk.ui.health

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.mumuk.databinding.FragmentHealthStep1Binding
import com.google.android.material.button.MaterialButton

class HealthStep1Fragment : Fragment() {

    private var _binding: FragmentHealthStep1Binding? = null
    private val binding get() = _binding!!

    private val healthViewModel: HealthViewModel by activityViewModels()

    private val allergyButtonsMap by lazy {
        mapOf(
            binding.btnShellfish to "갑각류",
            binding.btnNuts to "견과류",
            binding.btnDairy to "유제품",
            binding.btnWheat to "밀가루",
            binding.btnEgg to "계란",
            binding.btnFish to "생선",
            binding.btnSoy to "대두",
            binding.btnNone to "없음"
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHealthStep1Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupAllergyButtons()
        setupCustomAllergyInput()
        observeViewModel()
    }

    private fun observeViewModel() {
        healthViewModel.allergies.observe(viewLifecycleOwner) { allergies ->
            allergyButtonsMap.forEach { (button, allergy) ->
                button.isChecked = allergies.contains(allergy)
            }
        }
        healthViewModel.customAllergy.observe(viewLifecycleOwner) { custom ->
            if (binding.etCustomAllergy.text.toString() != custom) {
                binding.etCustomAllergy.setText(custom)
            }
        }
    }

    private fun setupAllergyButtons() {
        allergyButtonsMap.forEach { (button, allergyType) ->
            button.setOnClickListener {
                toggleAllergy(allergyType)
            }
        }
    }

    private fun toggleAllergy(allergyType: String) {
        val currentAllergies = healthViewModel.allergies.value?.toMutableSet() ?: mutableSetOf()

        if (allergyType == "없음") {
            if (!currentAllergies.contains("없음")) {
                currentAllergies.clear()
                currentAllergies.add("없음")
                healthViewModel.customAllergy.value = "" // 커스텀 입력 초기화
            } else {
                currentAllergies.remove("없음")
            }
        } else {
            if (currentAllergies.contains(allergyType)) {
                currentAllergies.remove(allergyType)
            } else {
                currentAllergies.add(allergyType)
                currentAllergies.remove("없음") // 다른 알러지 선택 시 '없음' 해제
            }
        }
        healthViewModel.allergies.value = currentAllergies
    }

    private fun setupCustomAllergyInput() {
        binding.etCustomAllergy.addTextChangedListener { s ->
            val input = s?.toString()?.trim()
            healthViewModel.customAllergy.value = input

            if (!input.isNullOrEmpty()) {
                val currentAllergies = healthViewModel.allergies.value?.toMutableSet() ?: mutableSetOf()
                if (currentAllergies.contains("없음")) {
                    currentAllergies.remove("없음")
                    healthViewModel.allergies.value = currentAllergies
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}