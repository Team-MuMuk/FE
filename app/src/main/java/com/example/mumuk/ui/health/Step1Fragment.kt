package com.example.mumuk.ui.health

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.mumuk.databinding.FragmentStep1Binding
import com.google.android.material.button.MaterialButton

class Step1Fragment : Fragment() {

    private var _binding: FragmentStep1Binding? = null
    private val binding get() = _binding!!

    private val selectedAllergies = mutableSetOf<String>()
    private var customAllergy: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStep1Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupAllergyButtons()
        setupCustomAllergyInput()
    }

    private fun setupAllergyButtons() {
        val allergyButtons = listOf(
            binding.btnShellfish to "갑각류",
            binding.btnNuts to "견과류",
            binding.btnDairy to "유제품",
            binding.btnWheat to "밀가루",
            binding.btnEgg to "계란",
            binding.btnFish to "생선",
            binding.btnSoy to "대두",
            binding.btnNone to "없음"
        )

        allergyButtons.forEach { (button, allergyType) ->
            button.setOnClickListener {
                toggleAllergy(button, allergyType)
            }
        }
    }

    private fun toggleAllergy(button: MaterialButton, allergyType: String) {
        // "없습니다" 버튼을 클릭한 경우
        if (allergyType == "없음") {
            if (button.isChecked) {
                // "없습니다"를 선택하면 다른 모든 알레르기 해제
                clearAllAllergies()
                selectedAllergies.add(allergyType)
                button.isChecked = true
            } else {
                selectedAllergies.remove(allergyType)
            }
        } else {
            // 다른 알레르기를 선택한 경우
            if (button.isChecked) {
                selectedAllergies.add(allergyType)
                // "없습니다" 버튼이 선택되어 있다면 해제
                if (selectedAllergies.contains("없음")) {
                    selectedAllergies.remove("없음")
                    binding.btnNone.isChecked = false
                }
            } else {
                selectedAllergies.remove(allergyType)
            }
        }

        updateAllergyDisplay()
    }

    private fun clearAllAllergies() {
        selectedAllergies.clear()

        // 모든 버튼 해제
        binding.btnShellfish.isChecked = false
        binding.btnNuts.isChecked = false
        binding.btnDairy.isChecked = false
        binding.btnWheat.isChecked = false
        binding.btnEgg.isChecked = false
        binding.btnFish.isChecked = false
        binding.btnSoy.isChecked = false
        binding.btnNone.isChecked = false
    }

    private fun setupCustomAllergyInput() {
        binding.etCustomAllergy.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val input = s?.toString()?.trim()
                customAllergy = if (input.isNullOrEmpty()) null else input

                // 커스텀 알레르기를 입력하면 "없습니다" 해제
                if (!customAllergy.isNullOrEmpty() && selectedAllergies.contains("없음")) {
                    selectedAllergies.remove("없음")
                    binding.btnNone.isChecked = false
                }

                updateAllergyDisplay()
            }
        })
    }

    private fun updateAllergyDisplay() {
        val allAllergies = mutableListOf<String>()
        allAllergies.addAll(selectedAllergies)

        customAllergy?.let {
            if (it.isNotEmpty()) allAllergies.add(it)
        }

        println("현재 선택된 알레르기: ${allAllergies.joinToString(", ")}")
    }

    // 외부에서 데이터를 가져올 수 있는 메서드들
    fun getSelectedAllergies(): Set<String> = selectedAllergies.toSet()
    fun getCustomAllergy(): String? = customAllergy
    fun getAllAllergies(): List<String> {
        val allAllergies = mutableListOf<String>()
        allAllergies.addAll(selectedAllergies)
        customAllergy?.let {
            if (it.isNotEmpty()) allAllergies.add(it)
        }
        return allAllergies
    }

    fun hasNoAllergies(): Boolean = selectedAllergies.contains("없음")

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}