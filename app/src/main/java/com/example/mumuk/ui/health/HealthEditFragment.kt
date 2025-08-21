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
import com.example.mumuk.data.api.AllergyApiService
import com.example.mumuk.data.api.HealthApiService
import com.example.mumuk.data.api.RetrofitClient
import com.example.mumuk.data.api.TokenManager
import com.example.mumuk.data.model.allergy.AllergyOptionsResponse
import com.example.mumuk.data.model.allergy.ToggleAllergyRequest
import com.example.mumuk.data.model.allergy.ToggleAllergyResponse
import com.example.mumuk.data.model.health.HealthGoalsResponse
import com.example.mumuk.data.model.health.ToggleHealthGoalRequest
import com.example.mumuk.data.model.health.ToggleHealthGoalResponse
import com.google.android.material.button.MaterialButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HealthEditFragment : Fragment() {
    private var _binding: FragmentHealthEditBinding? = null
    private val binding get() = _binding!!

    private var isAllergyEditSelected = false
    private var isGoalEditSelected = false

    private lateinit var allergyButtonMap: Map<String, MaterialButton>
    private lateinit var allergyTypeByButton: Map<MaterialButton, String>
    private lateinit var allergyApi: AllergyApiService

    private lateinit var goalButtonMap: Map<String, MaterialButton>
    private lateinit var goalTypeByButton: Map<MaterialButton, String>
    private lateinit var healthApi: HealthApiService

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHealthEditBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setNicknameForHealthTitle()

        binding.backBtn.setOnClickListener {
            findNavController().popBackStack()
        }

        // 맵과 API 인스턴스 초기화
        allergyButtonMap = mapOf(
            "SHELLFISH" to binding.btnShellfish,
            "NUTS" to binding.btnNuts,
            "DAIRY" to binding.btnDairy,
            "WHEAT" to binding.btnWheat,
            "EGG" to binding.btnEgg,
            "FISH" to binding.btnFish,
            "SOY" to binding.btnSoy,
            "NONE" to binding.btnNone
        )
        allergyTypeByButton = allergyButtonMap.entries.associate { (type, btn) -> btn to type }
        allergyApi = RetrofitClient.getAllergyApi(requireContext())

        binding.cardViewAllergyEdit.setOnClickListener {
            val wasSelected = isAllergyEditSelected
            isAllergyEditSelected = !isAllergyEditSelected
            toggleCard(
                selected = isAllergyEditSelected,
                card = binding.cardViewAllergyEdit,
                layout = binding.layoutAllergyEdit,
                textView = binding.tvAllergyGoal,
                imageView = binding.ivAllergyGoal,
                defaultText = "수정"
            )

            // "완료" -> "수정" 전환 시에만 서버에 변경 반영
            if (!isAllergyEditSelected && wasSelected) {
                val selectedTypes = allergyTypeByButton.filter { it.key.isChecked }.values.toList()
                android.util.Log.d("HealthEditFragment", "선택된 allergyType: $selectedTypes")
                val request = ToggleAllergyRequest(allergyTypeList = selectedTypes)
                allergyApi.toggleAllergies(request).enqueue(object : Callback<ToggleAllergyResponse> {
                    override fun onResponse(
                        call: Call<ToggleAllergyResponse>,
                        response: Response<ToggleAllergyResponse>
                    ) {
                        android.util.Log.d("HealthEditFragment", "toggleAllergies API 응답: isSuccessful=${response.isSuccessful}, code=${response.code()}, body=${response.body()}")
                        if (response.isSuccessful) {
                            val latestAllergies = response.body()?.data?.allergyOptions?.map { it.allergyType } ?: emptyList()
                            android.util.Log.d("HealthEditFragment", "서버 반영된 allergyType: $latestAllergies")
                            allergyButtonMap.forEach { (type, btn) ->
                                btn.isChecked = latestAllergies.contains(type)
                                updateAllergyButtonColor(btn)
                            }
                        } else {
                            android.util.Log.e("HealthEditFragment", "toggleAllergies 실패: code=${response.code()}, errorBody=${response.errorBody()?.string()}")
                        }
                    }
                    override fun onFailure(call: Call<ToggleAllergyResponse>, t: Throwable) {
                        android.util.Log.e("HealthEditFragment", "toggleAllergies API 호출 실패", t)
                        // TODO: 에러 처리
                    }
                })
            }
        }

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
            binding.btnEgg, binding.btnFish, binding.btnSoy
        )
        val noneAllergyButton = binding.btnNone

        allergyButtons.forEach { btn ->
            btn.setOnClickListener {
                // "수정" 상태가 아닐 때만 선택 토글
                if (!isAllergyEditSelected) {
                    btn.isChecked = !btn.isChecked
                }
                // "없습니다"가 체크되어 있으면 해제
                if (noneAllergyButton.isChecked && btn.isChecked) {
                    noneAllergyButton.isChecked = false
                    updateAllergyButtonColor(noneAllergyButton)
                }
                updateAllergyButtonColor(btn)
            }
            updateAllergyButtonColor(btn)
        }
        noneAllergyButton.setOnClickListener {
            if (!isAllergyEditSelected) {
                noneAllergyButton.isChecked = !noneAllergyButton.isChecked
            }
            if (noneAllergyButton.isChecked) {
                allergyButtons.forEach { btn ->
                    btn.isChecked = false
                    updateAllergyButtonColor(btn)
                }
            }
            updateAllergyButtonColor(noneAllergyButton)
        }
        updateAllergyButtonColor(noneAllergyButton)

        val goalButtons = listOf(
            binding.btnWeightLoss, binding.btnMuscleGain, binding.btnSugarReduction,
            binding.btnBloodPressure, binding.btnCholesterol, binding.btnDigestiveHealth
        )
        val noneGoalButton = binding.btnNoneGoal

        goalButtons.forEach { btn ->
            btn.setOnClickListener {
                if (!isGoalEditSelected) {
                    btn.isChecked = !btn.isChecked
                }
                // "없습니다"가 체크되어 있으면 해제
                if (noneGoalButton.isChecked && btn.isChecked) {
                    noneGoalButton.isChecked = false
                    updateGoalButtonColor(noneGoalButton)
                }
                updateGoalButtonColor(btn)
            }
            updateGoalButtonColor(btn)
        }
        noneGoalButton.setOnClickListener {
            if (!isGoalEditSelected) {
                noneGoalButton.isChecked = !noneGoalButton.isChecked
            }
            if (noneGoalButton.isChecked) {
                goalButtons.forEach { btn ->
                    btn.isChecked = false
                    updateGoalButtonColor(btn)
                }
            }
            updateGoalButtonColor(noneGoalButton)
        }
        updateGoalButtonColor(noneGoalButton)

        // 처음 진입 시 서버에서 알레르기 상태 조회
        allergyApi.getAllergyOptions()
            .enqueue(object : Callback<AllergyOptionsResponse> {
                override fun onResponse(
                    call: Call<AllergyOptionsResponse>,
                    response: Response<AllergyOptionsResponse>
                ) {
                    if (response.isSuccessful) {
                        val options = response.body()?.data?.allergyOptions ?: emptyList()
                        options.forEach { option ->
                            allergyButtonMap[option.allergyType]?.let { btn ->
                                btn.isChecked = true
                                updateAllergyButtonColor(btn)
                            }
                        }
                    }
                }
                override fun onFailure(call: Call<AllergyOptionsResponse>, t: Throwable) {
                    // TODO: 에러 처리
                }
            })

        healthApi = RetrofitClient.getHealthApi(requireContext())

        binding.cardViewGoalEdit.setOnClickListener {
            val wasSelected = isGoalEditSelected
            isGoalEditSelected = !isGoalEditSelected
            toggleCard(
                selected = isGoalEditSelected,
                card = binding.cardViewGoalEdit,
                layout = binding.layoutGoalEdit,
                textView = binding.tvGoalEdit,
                imageView = binding.ivGoalEdit,
                defaultText = "수정"
            )

            // "완료"에서 "수정" 전환 시 서버에 변경 반영
            if (!isGoalEditSelected && wasSelected) {
                val selectedTypes = goalTypeByButton.filter { it.key.isChecked }.values.toList()
                android.util.Log.d("HealthEditFragment", "선택된 healthGoalType: $selectedTypes")
                val request = ToggleHealthGoalRequest(healthGoalTypeList = selectedTypes)
                healthApi.toggleHealthGoals(request).enqueue(object : Callback<ToggleHealthGoalResponse> {
                    override fun onResponse(
                        call: Call<ToggleHealthGoalResponse>,
                        response: Response<ToggleHealthGoalResponse>
                    ) {
                        android.util.Log.d("HealthEditFragment", "toggleHealthGoals API 응답: isSuccessful=${response.isSuccessful}, code=${response.code()}, body=${response.body()}")
                        if (response.isSuccessful) {
                            val latestGoals = response.body()?.data?.healthGoalList?.map { it.healthGoalType } ?: emptyList()
                            android.util.Log.d("HealthEditFragment", "서버 반영된 healthGoalType: $latestGoals")
                            goalButtonMap.forEach { (type, btn) ->
                                btn.isChecked = latestGoals.contains(type)
                                updateGoalButtonColor(btn)
                            }
                        } else {
                            android.util.Log.e("HealthEditFragment", "toggleHealthGoals 실패: code=${response.code()}, errorBody=${response.errorBody()?.string()}")
                        }
                    }
                    override fun onFailure(call: Call<ToggleHealthGoalResponse>, t: Throwable) {
                        android.util.Log.e("HealthEditFragment", "toggleHealthGoals API 호출 실패", t)
                        // TODO: 에러 처리
                    }
                })
            }
        }

        // ... 버튼 초기화, 서버 조회 등 기존 코드 유지
        goalButtonMap = mapOf(
            "WEIGHT_LOSS" to binding.btnWeightLoss,
            "MUSCLE_GAIN" to binding.btnMuscleGain,
            "SUGAR_REDUCTION" to binding.btnSugarReduction,
            "BLOOD_PRESSURE" to binding.btnBloodPressure,
            "CHOLESTEROL" to binding.btnCholesterol,
            "DIGESTIVE_HEALTH" to binding.btnDigestiveHealth,
            "NONE" to binding.btnNoneGoal
        )
        goalTypeByButton = goalButtonMap.entries.associate { (type, btn) -> btn to type }

        healthApi.getHealthGoals()
            .enqueue(object : Callback<HealthGoalsResponse> {
                override fun onResponse(
                    call: Call<HealthGoalsResponse>,
                    response: Response<HealthGoalsResponse>
                ) {
                    if (response.isSuccessful) {
                        val goals = response.body()?.data?.healthGoalList ?: emptyList()
                        goals.forEach { goal ->
                            goalButtonMap[goal.healthGoalType]?.let { btn ->
                                btn.isChecked = true
                                updateGoalButtonColor(btn)
                            }
                        }
                    }
                }
                override fun onFailure(call: Call<HealthGoalsResponse>, t: Throwable) {
                    // TODO: 에러 처리
                }
            })
    }

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

    private fun updateAllergyButtonColor(btn: MaterialButton) {
        if (btn.isChecked) {
            btn.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.beige_500))
            btn.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
        } else {
            btn.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white))
            btn.setTextColor(ContextCompat.getColor(requireContext(), R.color.beige_500))
        }
    }

    private fun updateGoalButtonColor(btn: MaterialButton) {
        if (btn.isChecked) {
            btn.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.beige_500))
            btn.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
        } else {
            btn.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white))
            btn.setTextColor(ContextCompat.getColor(requireContext(), R.color.beige_500))
        }
    }

    private fun setNicknameForHealthTitle() {
        val loginType = TokenManager.getLoginType(requireContext()) ?: "LOCAL"

        if (loginType == "KAKAO" || loginType == "NAVER") {
            val savedNickname = TokenManager.getNickName(requireContext())
            val nickname = if (!savedNickname.isNullOrBlank()) savedNickname else "사용자"
            binding.textView50.text = "${nickname}님이 설정한 건강정보입니다"
        } else {
            RetrofitClient.getUserApi(requireContext()).getUserProfile()
                .enqueue(object : Callback<com.example.mumuk.data.model.mypage.UserProfileResponse> {
                    override fun onResponse(
                        call: Call<com.example.mumuk.data.model.mypage.UserProfileResponse>,
                        response: Response<com.example.mumuk.data.model.mypage.UserProfileResponse>
                    ) {
                        if (response.isSuccessful) {
                            val profile = response.body()?.data
                            val nickname = profile?.nickName?.takeIf { it.isNotBlank() } ?: "사용자"
                            binding.textView50.text = "${nickname}님이 설정한 건강정보입니다"
                        } else {
                            binding.textView50.text = "사용자님이 설정한 건강정보입니다"
                        }
                    }
                    override fun onFailure(
                        call: Call<com.example.mumuk.data.model.mypage.UserProfileResponse>,
                        t: Throwable
                    ) {
                        binding.textView50.text = "사용자님이 설정한 건강정보입니다"
                    }
                })
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}