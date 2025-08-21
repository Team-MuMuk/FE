package com.example.mumuk.ui.ingredient

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.example.mumuk.R
import com.example.mumuk.databinding.FragmentIngredientDetailBinding
import com.example.mumuk.data.model.Ingredient
import android.app.Dialog
import android.graphics.drawable.ColorDrawable
import android.view.Window
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.mumuk.data.api.RetrofitClient
import com.example.mumuk.data.model.ingredient.*
import com.example.mumuk.data.repository.IngredientRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.launch

class IngredientDetailFragment : Fragment() {
    private var _binding: FragmentIngredientDetailBinding? = null
    private val binding get() = _binding!!
    private var selectedDday: String? = null
    private var selectedIdx: Int? = null

    companion object {
        private const val TAG = "IngredientDetailFragment"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentIngredientDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val ingredient = arguments?.getSerializable("ingredient") as? Ingredient
        ingredient?.let {
            binding.name.text = it.name
            val expiryDate = it.expiryDate
            expiryDate?.let { dateStr ->
                val parts = dateStr.split("-")
                if (parts.size == 3) {
                    binding.year.setText(parts[0])
                    binding.month.setText(parts[1])
                    binding.day.setText(parts[2])
                }
            }
        }

        val cardViews = listOf(
            binding.d3 to binding.textView43,
            binding.d7 to binding.textView45,
            binding.d10 to binding.textView46,
            binding.d31 to binding.textView47,
            binding.none to binding.textView48
        )
        val ddayValues = listOf("D3", "D7", "D10", "D31", "NONE")

        val selectedCardColor = ContextCompat.getColor(requireContext(), R.color.beige_600)
        val defaultCardColor = ContextCompat.getColor(requireContext(), R.color.beige_100)
        val selectedTextColor = ContextCompat.getColor(requireContext(), R.color.white)
        val defaultTextColor = ContextCompat.getColor(requireContext(), R.color.black_400)

        fun updateCardViewUI(selectedIdx: Int?) {
            cardViews.forEachIndexed { idx, pair ->
                val (card, text) = pair
                if (selectedIdx != null && idx == selectedIdx) {
                    card.setCardBackgroundColor(selectedCardColor)
                    text.setTextColor(selectedTextColor)
                } else {
                    card.setCardBackgroundColor(defaultCardColor)
                    text.setTextColor(defaultTextColor)
                }
            }
        }
        
        updateCardViewUI(null)
        binding.notiBtn.isEnabled = false

        cardViews.forEachIndexed { idx, pair ->
            pair.first.setOnClickListener {
                selectedIdx = idx
                selectedDday = ddayValues[idx]
                updateCardViewUI(selectedIdx)
                binding.notiBtn.isEnabled = true
            }
        }

        binding.backBtn.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.notiBtn.setOnClickListener {
            val ingredient = arguments?.getSerializable("ingredient") as? Ingredient
            val dday = selectedDday
            if (ingredient == null || dday == null) {
                Toast.makeText(requireContext(), "재료와 알림 기간을 선택해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            lifecycleScope.launch {
                val request = IngredientDdaySettingRequest(daySetting = listOf(dday))
                try {
                    val response = RetrofitClient.getIngredientApi(requireContext())
                        .updateIngredientDdaySetting(ingredient.id, request)
                    if (response.isSuccessful) {
                        showPushAgreeDialog()
                    } else {
                        Toast.makeText(requireContext(), "알림 기간 설정에 실패했습니다.", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(requireContext(), "알림 기간 설정 중 오류 발생", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.refreshBtn.setOnClickListener {
            val ingredient = arguments?.getSerializable("ingredient") as? Ingredient
            if (ingredient != null) {
                val year = binding.year.text.toString()
                val month = binding.month.text.toString().padStart(2, '0')
                val day = binding.day.text.toString().padStart(2, '0')
                val expireDate = "$year-$month-$day"

                lifecycleScope.launch {
                    val repository = IngredientRepository(requireContext())
                    val response = repository.updateIngredientExpireDateRaw(ingredient.id, expireDate)
                    if (response.isSuccessful) {
                        showExpireDateUpdatedDialog()
                    } else {
                        val errorBody = response.errorBody()?.string()
                        val errorMsg = if (errorBody?.contains("유통기한이 유효하지 않습니다") == true) {
                            "이전 날짜로는 재설정이 불가능합니다!"
                        } else {
                            "유통기한 수정에 실패했습니다."
                        }
                        showExpireDateErrorDialog(errorMsg)
                    }
                }
            } else {
                Toast.makeText(requireContext(), "재료 정보를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showPushAgreeDialog() {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_ingredient_alarm_push)
        dialog.setCancelable(true)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        val btnOk = dialog.findViewById<TextView>(R.id.btnOk)
        btnOk.setOnClickListener {
            Log.d(TAG, "푸시 알림 동의 다이얼로그 확인 버튼 클릭됨")
            pushAgreeApi {
                getFcmTokenAndSave { fcmTokenSaved ->
                    if (fcmTokenSaved) {
                        Toast.makeText(requireContext(), "푸시 알림 동의 및 토큰 저장 완료!", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(requireContext(), "FCM 토큰 저장에 실패했습니다.", Toast.LENGTH_SHORT).show()
                    }
                    dialog.dismiss()
                }
            }
        }

        dialog.show()
    }

    private fun pushAgreeApi(onComplete: () -> Unit) {
        Log.d(TAG, "푸시 알림 동의 API 호출 시작")
        val pushAlarmApi = RetrofitClient.getPushAlarmApi(requireContext())
        val request = PushAgreeRequest(fcmAgreed = true)
        pushAlarmApi.pushAgree(request).enqueue(object : Callback<PushAgreeResponse> {
            override fun onResponse(
                call: Call<PushAgreeResponse>,
                response: Response<PushAgreeResponse>
            ) {
                if (response.isSuccessful) {
                    Log.d(TAG, "푸시 알림 동의 API 호출 성공: ${response.body()}")
                } else {
                    Log.e(TAG, "푸시 알림 동의 API 응답 실패: ${response.code()} ${response.errorBody()?.string()}")
                }
                onComplete()
            }

            override fun onFailure(call: Call<PushAgreeResponse>, t: Throwable) {
                Log.e(TAG, "푸시 알림 동의 API 네트워크 오류", t)
                Toast.makeText(requireContext(), "알림 동의 요청에 실패했습니다.", Toast.LENGTH_SHORT).show()
                onComplete()
            }
        })
    }

    private fun getFcmTokenAndSave(onComplete: (Boolean) -> Unit) {
        Log.d(TAG, "getFcmTokenAndSave() 호출됨")
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result
                Log.d(TAG, "FCM 토큰 획득 성공: $token")
                saveFcmToken(token) {
                    onComplete(true)
                }
            } else {
                Log.e(TAG, "FCM 토큰 획득 실패: ${task.exception}")
                onComplete(false)
            }
        }
    }

    private fun saveFcmToken(fcmToken: String, onComplete: () -> Unit) {
        Log.d(TAG, "FCM 토큰 저장 API 호출 시작, 저장할 fcmToken: $fcmToken")
        val pushFcmTokenApi = RetrofitClient.getPushFcmTokenApi(requireContext())
        val request = PushFcmTokenRequest(fcmToken = fcmToken)
        Log.d(TAG, "saveFcmToken() - Request Body: $request")
        pushFcmTokenApi.saveFcmToken(request).enqueue(object : Callback<PushFcmTokenResponse> {
            override fun onResponse(
                call: Call<PushFcmTokenResponse>,
                response: Response<PushFcmTokenResponse>
            ) {
                Log.d(TAG, "saveFcmToken - API 응답 isSuccessful: ${response.isSuccessful}")
                if (response.isSuccessful) {
                    Log.d(TAG, "FCM 토큰 저장 API 성공: ${response.body()}")
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e(TAG, "FCM 토큰 저장 API 응답 실패: ${response.code()} ${errorBody}")
                }
                onComplete()
            }

            override fun onFailure(call: Call<PushFcmTokenResponse>, t: Throwable) {
                Log.e(TAG, "FCM 토큰 저장 API 네트워크 오류", t)
                onComplete()
            }
        })
    }

    private fun showExpireDateUpdatedDialog() {
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.dialog_ingredient_refresh)
        dialog.setCancelable(false)

        dialog.window?.setBackgroundDrawable(ColorDrawable(android.graphics.Color.TRANSPARENT))
        dialog.window?.setDimAmount(0f)
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )

        val btnOk = dialog.findViewById<TextView>(R.id.btnOk)
        btnOk.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }

    private fun showExpireDateErrorDialog(message: String) {
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.dialog_ingredient_refresh)
        dialog.setCancelable(false)

        dialog.window?.setBackgroundDrawable(ColorDrawable(android.graphics.Color.TRANSPARENT))
        dialog.window?.setDimAmount(0f)
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )

        val msgTextView = dialog.findViewById<TextView>(R.id.textView)
        msgTextView.text = message

        val btnOk = dialog.findViewById<TextView>(R.id.btnOk)
        btnOk.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}