package com.example.mumuk.ui.health

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.mumuk.databinding.FragmentStep0Binding
import com.google.android.material.button.MaterialButton

class Step0Fragment : Fragment() {

    private var _binding: FragmentStep0Binding? = null
    private val binding get() = _binding!!

    private var selectedGender: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStep0Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupGenderButtons()
    }

    private fun setupGenderButtons() {
        // 성별 버튼들 (실제 버튼 ID에 맞게 수정)
        binding.btnMale.setOnClickListener {
            selectGender("male", binding.btnMale)
        }

        binding.btnFemale.setOnClickListener {
            selectGender("female", binding.btnFemale)
        }
    }

    private fun selectGender(gender: String, selectedButton: MaterialButton) {
        selectedGender = gender

        // 모든 버튼 해제
        binding.btnMale.isChecked = false
        binding.btnFemale.isChecked = false

        // 선택된 버튼만 체크
        selectedButton.isChecked = true

        // SharedPreferences에 성별 정보 저장
        saveGenderToPreferences(gender)

        println("선택된 성별: $gender")
    }

    private fun saveGenderToPreferences(gender: String) {
        val sharedPref = requireContext().getSharedPreferences("health_data", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putString("selected_gender", gender)
            apply()
        }
    }

    fun getSelectedGender(): String? = selectedGender

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}