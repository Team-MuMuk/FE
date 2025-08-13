package com.example.mumuk.ui.recommend

import android.animation.ObjectAnimator
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.mumuk.R
import com.example.mumuk.data.model.Recipe
import com.example.mumuk.data.repository.HealthAiRecipeRepository
import com.example.mumuk.databinding.FragmentHealthRecommendBinding
import kotlinx.coroutines.launch

class HealthRecommendFragment : Fragment() {
    private var _binding: FragmentHealthRecommendBinding? = null
    private val binding get() = _binding!!

    private val healthAiRepository by lazy { HealthAiRecipeRepository(requireContext()) }

    private lateinit var aiRecipeAdapter: HealthAiAdapter
    private var aiRecipeList: List<Recipe> = emptyList()
    private var isExpanded = false

    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            uploadImageToServer(it)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (aiRecipeList.isEmpty()) {
            loadAiRecipes()
        }
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
        updateAiRecipeList()

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
    }

    private fun setupRecyclerView() {
        aiRecipeAdapter = HealthAiAdapter(
            mutableListOf(),
            onItemClick = { recipe ->
                Log.d("HealthRecommend", "Recipe clicked. ID: ${recipe.id}")
                val bundle = bundleOf("recipeId" to recipe.id)
                findNavController().navigate(R.id.action_healthRecommendFragment_to_recipeFragment, bundle)
            },
            onHeartClick = { _, _ ->
                // TODO: 좋아요 기능 구현
            }
        )

        binding.aiRecipeRV.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.aiRecipeRV.adapter = aiRecipeAdapter
    }

    private fun loadAiRecipes() {
        lifecycleScope.launch {
            try {
                aiRecipeList = healthAiRepository.getAiRecipes()
                if (_binding != null) {
                    updateAiRecipeList()
                }
            } catch (e: Exception) {
                Log.e("HealthRecommend", "Failed to load AI recipes", e)
                if (context != null) {
                    Toast.makeText(requireContext(), "추천 레시피를 불러오는데 실패했습니다.", Toast.LENGTH_SHORT).show()
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
        aiRecipeAdapter.updateList(itemsToShow.toMutableList())
        binding.plusBtn.visibility = if (aiRecipeList.size > 6 && !isExpanded) View.VISIBLE else View.GONE
    }

    private fun uploadImageToServer(uri: Uri) {
        // TODO: 서버로 이미지를 업로드하는 코드 구현
        showAiRecommendDialog()
    }

    private fun showAiRecommendDialog() {
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.dialog_ai_recommend)
        dialog.setCancelable(false)

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.setDimAmount(0f)
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

        Handler(Looper.getMainLooper()).postDelayed({
            if (dialog.isShowing) {
                dialog.dismiss()
            }
        }, 3000)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}