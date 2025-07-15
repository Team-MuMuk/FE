package com.example.mumuk.ui.health

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.mumuk.R
import com.example.mumuk.databinding.FragmentStep2Binding
import com.google.android.material.button.MaterialButton

class Step2Fragment : Fragment() {

    private var _binding: FragmentStep2Binding? = null
    private val binding get() = _binding!!

    private val selectedGoals = mutableSetOf<String>()
    private var customGoal: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStep2Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupGoalButtons()
        setupCustomGoalInput()
    }

    private fun setupGoalButtons() {
        val goalButtons = listOf(
            binding.btnWeightLoss to "체중감량",
            binding.btnMuscleGain to "근육량 증가",
            binding.btnSugarReduction to "당 줄이기",
            binding.btnBloodPressure to "혈압관리",
            binding.btnCholesterol to "콜레스테롤 관리",
            binding.btnDigestiveHealth to "소화 및 장 건강",
            binding.btnNone to "없음"
        )

        goalButtons.forEach { (button, goalType) ->
            button.setOnClickListener {
                toggleGoal(button, goalType)
            }
        }
    }

    private fun toggleGoal(button: MaterialButton, goalType: String) {
        // "없습니다" 버튼을 클릭한 경우
        if (goalType == "없음") {
            if (button.isChecked) {
                // "없습니다"를 선택하면 다른 모든 목표 해제
                clearAllGoals()
                selectedGoals.add(goalType)
                button.isChecked = true
            } else {
                selectedGoals.remove(goalType)
            }
        } else {
            // 다른 건강목표를 선택한 경우
            if (button.isChecked) {
                selectedGoals.add(goalType)
                // "없습니다" 버튼이 선택되어 있다면 해제
                if (selectedGoals.contains("없음")) {
                    selectedGoals.remove("없음")
                    binding.btnNone.isChecked = false
                }
            } else {
                selectedGoals.remove(goalType)
            }
        }

        updateGoalDisplay()
    }

    private fun clearAllGoals() {
        selectedGoals.clear()

        // 모든 버튼 해제
        binding.btnWeightLoss.isChecked = false
        binding.btnMuscleGain.isChecked = false
        binding.btnSugarReduction.isChecked = false
        binding.btnBloodPressure.isChecked = false
        binding.btnCholesterol.isChecked = false
        binding.btnDigestiveHealth.isChecked = false
        binding.btnNone.isChecked = false
    }

    private fun setupCustomGoalInput() {
        binding.etCustomGoal.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val input = s?.toString()?.trim()
                customGoal = if (input.isNullOrEmpty()) null else input

                // 커스텀 목표를 입력하면 "없습니다" 해제
                if (!customGoal.isNullOrEmpty() && selectedGoals.contains("없음")) {
                    selectedGoals.remove("없음")
                    binding.btnNone.isChecked = false
                }

                updateGoalDisplay()
            }
        })
    }

    private fun updateGoalDisplay() {
        val allGoals = mutableListOf<String>()
        allGoals.addAll(selectedGoals)

        customGoal?.let {
            if (it.isNotEmpty()) allGoals.add(it)
        }

        println("현재 선택된 건강목표: ${allGoals.joinToString(", ")}")
    }

    // 추가: Step2에서 완료 처리
    fun completeStep2() {
        // 데이터 저장 로직 (필요시)
        saveHealthData()

        // 완료 화면으로 이동
        findNavController().navigate(R.id.action_healthManagement_to_healthComplete)
    }

    fun saveHealthData() {
        // TODO: SharedPreferences나 ViewModel을 통해 데이터 저장
        val allGoals = getAllGoals()
        println("건강 데이터 저장: $allGoals")

        // 예시: SharedPreferences에 저장
        // val sharedPref = requireContext().getSharedPreferences("health_data", Context.MODE_PRIVATE)
        // with(sharedPref.edit()) {
        //     putStringSet("selected_goals", selectedGoals)
        //     putString("custom_goal", customGoal)
        //     apply()
        // }
    }

    // 외부에서 데이터를 가져올 수 있는 메서드들
    fun getSelectedGoals(): Set<String> = selectedGoals.toSet()
    fun getCustomGoal(): String? = customGoal
    fun getAllGoals(): List<String> {
        val allGoals = mutableListOf<String>()
        allGoals.addAll(selectedGoals)
        customGoal?.let {
            if (it.isNotEmpty()) allGoals.add(it)
        }
        return allGoals
    }

    fun hasNoGoals(): Boolean = selectedGoals.contains("없음")

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}