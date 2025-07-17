package com.example.mumuk.ui.health

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.mumuk.databinding.FragmentHealthStep0Binding
import com.google.android.material.button.MaterialButton

class HealthStep0Fragment : Fragment() {

    private var _binding: FragmentHealthStep0Binding? = null
    private val binding get() = _binding!!

    // activityViewModels()를 사용하여 ViewModel 공유
    private val healthViewModel: HealthViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHealthStep0Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupGenderButtons()
        setupInputListeners()
        observeViewModel()
    }

    private fun setupGenderButtons() {
        binding.btnMale.setOnClickListener {
            selectGender("male", binding.btnMale)
        }

        binding.btnFemale.setOnClickListener {
            selectGender("female", binding.btnFemale)
        }
    }

    private fun setupInputListeners() {
        binding.etHeight.addTextChangedListener { text ->
            healthViewModel.height.value = text?.toString()
        }
        binding.etWeight.addTextChangedListener { text ->
            healthViewModel.weight.value = text?.toString()
        }
    }

    private fun observeViewModel() {
        healthViewModel.gender.observe(viewLifecycleOwner) { gender ->
            binding.btnMale.isChecked = gender == "male"
            binding.btnFemale.isChecked = gender == "female"
        }
        healthViewModel.height.observe(viewLifecycleOwner) { height ->
            if (binding.etHeight.text.toString() != height) {
                binding.etHeight.setText(height)
            }
        }
        healthViewModel.weight.observe(viewLifecycleOwner) { weight ->
            if (binding.etWeight.text.toString() != weight) {
                binding.etWeight.setText(weight)
            }
        }
    }

    private fun selectGender(gender: String, selectedButton: MaterialButton) {
        // ViewModel의 gender 값을 업데이트
        healthViewModel.gender.value = gender
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}