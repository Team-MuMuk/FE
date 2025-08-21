package com.example.mumuk.ui.recommend

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mumuk.R
import com.example.mumuk.data.model.Recipe
import com.example.mumuk.data.repository.IngredientAiRecipeRepository
import com.example.mumuk.data.repository.IngredientRepository
import com.example.mumuk.databinding.FragmentIngredientRecommendBinding
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicInteger

class IngredientRecommendFragment : Fragment() {
    private var _binding: FragmentIngredientRecommendBinding? = null
    private val binding get() = _binding!!

    private val ingredientRepository by lazy { IngredientRepository(requireContext()) }
    private val aiRecipeRepository by lazy { IngredientAiRecipeRepository(requireContext()) }

    private lateinit var aiRecipeAdapter: IngredientAiRecipeAdapter
    private var aiRecipeList: List<Recipe> = emptyList()
    private var isExpanded = false

    private var imageLoadCounter: AtomicInteger? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentIngredientRecommendBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerViews()
        binding.loadingOverlay.show()
        loadIngredients()
        loadAiRecipes()

        binding.plusBtn.setOnClickListener {
            isExpanded = true
            updateAiRecipeList()
        }

        binding.backBtn.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun setupRecyclerViews() {
        binding.aiRecipeRV.layoutManager = GridLayoutManager(requireContext(), 2)
        aiRecipeAdapter = IngredientAiRecipeAdapter(
            mutableListOf(),
            onItemClick = { recipe ->
                Log.d("IngredientRecommend", "Recipe clicked. ID: ${recipe.id}")
                val bundle = bundleOf("recipeId" to recipe.id)
                findNavController().navigate(R.id.action_ingredientRecommendFragment_to_recipeFragment, bundle)
            },
            onImageLoaded = {
                if (imageLoadCounter?.decrementAndGet() == 0) {
                    binding.loadingOverlay.hide()
                }
            }
        )
        binding.aiRecipeRV.adapter = aiRecipeAdapter
    }

    private fun loadIngredients() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val ingredientList = ingredientRepository.getIngredients()
                binding.countRV.layoutManager = LinearLayoutManager(requireContext())
                binding.countRV.adapter = IngredientCountAdapter(
                    ingredientList,
                    onQuantityChanged = { ingredientId, newCount ->
                        viewLifecycleOwner.lifecycleScope.launch {
                            val success = ingredientRepository.updateIngredientQuantity(ingredientId, newCount)
                            if (!success) {
                                Toast.makeText(requireContext(), "수량 변경 실패", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                )
            } catch (e: Exception) {
                Log.e("IngredientRecommend", "Failed to load ingredients", e)
                Toast.makeText(requireContext(), "재료 목록을 불러오는데 실패했습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadAiRecipes() {
        lifecycleScope.launch {
            try {
                aiRecipeList = aiRecipeRepository.getAiRecipes()
                if (_binding != null) {
                    updateAiRecipeList()
                }
            } catch (e: Exception) {
                if (_binding != null) {
                    binding.loadingOverlay.hide()
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
            binding.loadingOverlay.hide()
        }

        aiRecipeAdapter.updateList(itemsToShow.toMutableList())
        binding.plusBtn.visibility = if (aiRecipeList.size > 6 && !isExpanded) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}