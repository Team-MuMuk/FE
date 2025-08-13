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
import android.view.Window
import android.widget.TextView
import android.widget.Toast
import com.example.mumuk.data.api.RetrofitClient
import com.example.mumuk.data.model.ingredient.PushAgreeResponse
import com.example.mumuk.data.model.ingredient.PushAgreeRequest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class IngredientDetailFragment : Fragment() {
    private var _binding: FragmentIngredientDetailBinding? = null
    private val binding get() = _binding!!

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
            val expiryDate = it.expiryDate // 예: "2025-10-10"
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

        val selectedCardColor = ContextCompat.getColor(requireContext(), R.color.beige_600)
        val defaultCardColor = ContextCompat.getColor(requireContext(), R.color.beige_100)
        val selectedTextColor = ContextCompat.getColor(requireContext(), R.color.white)
        val defaultTextColor = ContextCompat.getColor(requireContext(), R.color.black_400)

        cardViews.forEach { (card, text) ->
            card.setCardBackgroundColor(defaultCardColor)
            text.setTextColor(defaultTextColor)
        }

        fun selectCard(selectedIdx: Int) {
            cardViews.forEachIndexed { idx, pair ->
                val (card, text) = pair
                if (idx == selectedIdx) {
                    card.setCardBackgroundColor(selectedCardColor)
                    text.setTextColor(selectedTextColor)
                } else {
                    card.setCardBackgroundColor(defaultCardColor)
                    text.setTextColor(defaultTextColor)
                }
            }
        }

        cardViews.forEachIndexed { idx, pair ->
            pair.first.setOnClickListener {
                selectCard(idx)
            }
        }

        binding.backBtn.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.notiBtn.setOnClickListener {
            showPushAgreeDialog()
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
                Toast.makeText(requireContext(), "푸시 알림 동의가 완료되었습니다.", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}