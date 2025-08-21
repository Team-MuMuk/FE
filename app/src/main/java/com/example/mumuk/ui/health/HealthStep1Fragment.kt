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
            binding.btnShellfish to "SHELLFISH",
            binding.btnNuts to "NUTS",
            binding.btnDairy to "DAIRY",
            binding.btnWheat to "WHEAT",
            binding.btnEgg to "EGG",
            binding.btnFish to "FISH",
            binding.btnSoy to "SOY",
            binding.btnNone to "NONE"
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

        if (allergyType == "NONE") {
            if (!currentAllergies.contains("NONE")) {
                currentAllergies.clear()
                currentAllergies.add("NONE")
                healthViewModel.customAllergy.value = "" // 커스텀 입력 초기화
            } else {
                currentAllergies.remove("NONE")
            }
        } else {
            if (currentAllergies.contains(allergyType)) {
                currentAllergies.remove(allergyType)
            } else {
                currentAllergies.add(allergyType)
                currentAllergies.remove("NONE") // 다른 알러지 선택 시 '없음' 해제
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
                if (currentAllergies.contains("NONE")) {
                    currentAllergies.remove("NONE")
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