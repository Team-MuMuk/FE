package com.example.mumuk.ui.recommend

import android.animation.ObjectAnimator
import android.app.Dialog
import android.database.Cursor
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.mumuk.R
import com.example.mumuk.data.api.AllergyApiService
import com.example.mumuk.data.api.HealthApiService
import com.example.mumuk.data.api.RetrofitClient
import com.example.mumuk.data.model.Recipe
import com.example.mumuk.data.model.allergy.AllergyOptionsResponse
import com.example.mumuk.data.model.health.HealthGoalsResponse
import com.example.mumuk.data.repository.HealthAiRecipeRepository
import com.example.mumuk.data.repository.OcrRepository
import com.example.mumuk.databinding.FragmentHealthRecommendBinding
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.atomic.AtomicInteger

class HealthRecommendFragment : Fragment() {
    private var _binding: FragmentHealthRecommendBinding? = null
    private val binding get() = _binding!!

    private val healthAiRepository by lazy { HealthAiRecipeRepository(requireContext()) }
    private val ocrRepository by lazy { OcrRepository(requireContext()) }

    private lateinit var aiRecipeAdapter: HealthAiAdapter
    private var aiRecipeList: List<Recipe> = emptyList()
    private var isExpanded = false

    private lateinit var filterContainer: LinearLayout

    private lateinit var allergyApi: AllergyApiService
    private lateinit var healthApi: HealthApiService

    private var imageLoadCounter: AtomicInteger? = null
    private var onImagesLoadedAction: (() -> Unit)? = null

    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val fileName = getFileName(it)
            binding.imgText.text = fileName ?: "image.jpg"
            uploadImageToServer(it)
        }
    }

    private val allergyTypeKorMap = mapOf(
        "SHELLFISH" to "갑각류 알레르기",
        "NUTS" to "견과류 알레르기",
        "DAIRY" to "유제품 알레르기",
        "WHEAT" to "밀 알레르기",
        "EGG" to "계란 알레르기",
        "FISH" to "생선 알레르기",
        "SOY" to "콩 알레르기",
        "NONE" to "알레르기 없음"
    )
    private val healthGoalKorMap = mapOf(
        "WEIGHT_LOSS" to "체중감량",
        "MUSCLE_GAIN" to "근육증가",
        "SUGAR_REDUCTION" to "저당식",
        "BLOOD_PRESSURE" to "혈압관리",
        "CHOLESTEROL" to "콜레스테롤관리",
        "DIGESTIVE_HEALTH" to "소화건강",
        "NONE" to "목표없음"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHealthRecommendBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()

        binding.backBtn.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.plusBtn.setOnClickListener {
            isExpanded = true
            updateAiRecipeList()
        }

        binding.addImg.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        allergyApi = RetrofitClient.getAllergyApi(requireContext())
        healthApi = RetrofitClient.getHealthApi(requireContext())

        filterContainer = binding.root.findViewById(R.id.filterContainer)
        fetchAndDisplayFilters()

        binding.loadingOverlay.show()
        onImagesLoadedAction = {
            binding.loadingOverlay.hide()
        }
        loadAiRecipes()
    }

    private fun fetchAndDisplayFilters() {
        allergyApi.getAllergyOptions().enqueue(object : Callback<AllergyOptionsResponse> {
            override fun onResponse(
                call: Call<AllergyOptionsResponse>,
                response: Response<AllergyOptionsResponse>
            ) {
                val allergyList = response.body()?.data?.allergyOptions?.map {
                    allergyTypeKorMap[it.allergyType] ?: it.allergyType
                } ?: emptyList()
                healthApi.getHealthGoals().enqueue(object : Callback<HealthGoalsResponse> {
                    override fun onResponse(
                        call: Call<HealthGoalsResponse>,
                        response: Response<HealthGoalsResponse>
                    ) {
                        val goalList = response.body()?.data?.healthGoalList?.map {
                            healthGoalKorMap[it.healthGoalType] ?: it.healthGoalType
                        } ?: emptyList()
                        val filterList = allergyList + goalList
                        displayFilters(filterList)
                    }
                    override fun onFailure(call: Call<HealthGoalsResponse>, t: Throwable) {
                        displayFilters(allergyList)
                    }
                })
            }
            override fun onFailure(call: Call<AllergyOptionsResponse>, t: Throwable) {
                // TODO: 에러처리
            }
        })
    }

    private fun displayFilters(filters: List<String>) {
        binding.filterContainer.removeAllViews()
        val inflater = LayoutInflater.from(requireContext())
        filters.forEachIndexed { index, filterText ->
            val cardBinding = com.example.mumuk.databinding.ItemFilterCardBinding.inflate(inflater)
            cardBinding.filterText.text = filterText

            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            if (index == 0) {
                params.marginStart = dpToPx(20)
            } else {
                params.marginStart = 0
            }
            params.marginEnd = dpToPx(8)

            cardBinding.root.layoutParams = params

            binding.filterContainer.addView(cardBinding.root)
        }
    }

    private fun dpToPx(dp: Int): Int {
        return (dp * requireContext().resources.displayMetrics.density).toInt()
    }

    private fun setupRecyclerView() {
        aiRecipeAdapter = HealthAiAdapter(
            mutableListOf(),
            onItemClick = { recipe ->
                Log.d("HealthRecommend", "Recipe clicked. ID: ${recipe.id}")
                val bundle = bundleOf("recipeId" to recipe.id)
                findNavController().navigate(R.id.action_healthRecommendFragment_to_recipeFragment, bundle)
            },
            onImageLoaded = {
                if (imageLoadCounter?.decrementAndGet() == 0) {
                    onImagesLoadedAction?.invoke()
                    onImagesLoadedAction = null
                }
            }
        )

        binding.aiRecipeRV.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.aiRecipeRV.adapter = aiRecipeAdapter
    }

    private fun loadAiRecipes() {
        lifecycleScope.launch {
            try {
                aiRecipeList = healthAiRepository.getAiRecipes()
                isExpanded = false
                if (_binding != null) {
                    updateAiRecipeList()
                }
            } catch (e: Exception) {
                if (_binding != null) {
                    binding.loadingOverlay.hide()
                    onImagesLoadedAction?.invoke()
                    onImagesLoadedAction = null
                }
            }
        }
    }

    private fun updateAiRecipeList() {
        if (!this::aiRecipeAdapter.isInitialized) return

        val itemsToShow = if (aiRecipeList.size > 6 && !isExpanded) {
            aiRecipeList.take(6)
        } else {
            aiRecipeList
        }

        if (itemsToShow.isNotEmpty()) {
            imageLoadCounter = AtomicInteger(itemsToShow.size)
        } else {
            onImagesLoadedAction?.invoke()
            onImagesLoadedAction = null
        }

        aiRecipeAdapter.updateList(itemsToShow.toMutableList())
        binding.plusBtn.visibility = if (aiRecipeList.size > 6 && !isExpanded) View.VISIBLE else View.GONE
    }

    private fun uploadImageToServer(uri: Uri) {
        val dialog = showAiRecommendDialog()
        lifecycleScope.launch {
            try {
                ocrRepository.uploadImageForOcr(uri)
                Log.d("HealthRecommend", "OCR Success")

                onImagesLoadedAction = {
                    dialog.dismiss()
                }

                loadAiRecipes()

            } catch (e: Exception) {
                dialog.dismiss()
                Log.e("HealthRecommend", "Failed to upload or load recipes", e)
                if (context != null) {
                    Toast.makeText(requireContext(), "이미지 분석 또는 레시피 로딩에 실패했습니다.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun showAiRecommendDialog(): Dialog {
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.dialog_ai_recommend)
        dialog.setCancelable(false)

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.setDimAmount(0.5f)
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )

        val loadingImage = dialog.findViewById<ImageView>(R.id.loadingImage)
        loadingImage?.let {
            val animator = ObjectAnimator.ofFloat(it, "rotation", 0f, 360f)
            animator.duration = 1000
            animator.repeatCount = ObjectAnimator.INFINITE
            animator.start()
            dialog.setOnDismissListener { animator.cancel() }
        }

        dialog.show()
        return dialog
    }

    private fun getFileName(uri: Uri): String? {
        var result: String? = null
        if (uri.scheme == "content") {
            val cursor: Cursor? = context?.contentResolver?.query(uri, null, null, null, null)
            cursor.use {
                if (it != null && it.moveToFirst()) {
                    val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (nameIndex != -1) {
                        result = it.getString(nameIndex)
                    }
                }
            }
        }
        if (result == null) {
            result = uri.path
            val cut = result?.lastIndexOf('/')
            if (cut != -1) {
                if (cut != null) {
                    result = result?.substring(cut + 1)
                }
            }
        }
        return result
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}