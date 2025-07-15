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
import com.example.mumuk.databinding.FragmentHealthStep2Binding
import com.google.android.material.button.MaterialButton

class HealthStep2Fragment : Fragment() {

    private var _binding: FragmentHealthStep2Binding? = null
    private val binding get() = _binding!!

    private val healthViewModel: HealthViewModel by activityViewModels()

    private val goalButtonsMap by lazy {
        mapOf(
            binding.btnWeightLoss to "체중감량",
            binding.btnMuscleGain to "근육량 증가",
            binding.btnSugarReduction to "당 줄이기",
            binding.btnBloodPressure to "혈압관리",
            binding.btnCholesterol to "콜레스테롤 관리",
            binding.btnDigestiveHealth to "소화 및 장 건강",
            binding.btnNone to "없음"
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHealthStep2Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupGoalButtons()
        setupCustomGoalInput()
        observeViewModel()
    }

    private fun observeViewModel() {
        healthViewModel.goals.observe(viewLifecycleOwner) { goals ->
            goalButtonsMap.forEach { (button, goal) ->
                button.isChecked = goals.contains(goal)
            }
        }
        healthViewModel.customGoal.observe(viewLifecycleOwner) { custom ->
            if (binding.etCustomGoal.text.toString() != custom) {
                binding.etCustomGoal.setText(custom)
            }
        }
    }

    private fun setupGoalButtons() {
        goalButtonsMap.forEach { (button, goalType) ->
            button.setOnClickListener {
                toggleGoal(goalType)
            }
        }
    }

    private fun toggleGoal(goalType: String) {
        val currentGoals = healthViewModel.goals.value?.toMutableSet() ?: mutableSetOf()

        if (goalType == "없음") {
            if (!currentGoals.contains("없음")) {
                currentGoals.clear()
                currentGoals.add("없음")
                healthViewModel.customGoal.value = ""
            } else {
                currentGoals.remove("없음")
            }
        } else {
            if (currentGoals.contains(goalType)) {
                currentGoals.remove(goalType)
            } else {
                currentGoals.add(goalType)
                currentGoals.remove("없음")
            }
        }
        healthViewModel.goals.value = currentGoals
    }

    private fun setupCustomGoalInput() {
        binding.etCustomGoal.addTextChangedListener { s ->
            val input = s?.toString()?.trim()
            healthViewModel.customGoal.value = input

            if (!input.isNullOrEmpty()) {
                val currentGoals = healthViewModel.goals.value?.toMutableSet() ?: mutableSetOf()
                if (currentGoals.contains("없음")) {
                    currentGoals.remove("없음")
                    healthViewModel.goals.value = currentGoals
                }
            }
        }
    }

    fun saveHealthData() {
        // TODO: SharedPreferences나 다른 저장소에 ViewModel의 데이터를 저장
        val selectedGoals = healthViewModel.goals.value
        val customGoal = healthViewModel.customGoal.value
        println("건강 데이터 저장: Goals=$selectedGoals, CustomGoal=$customGoal")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}