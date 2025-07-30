package com.example.mumuk.ui.recommend

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.GridLayoutManager
import com.example.mumuk.R
import com.example.mumuk.data.model.Recipe
import com.example.mumuk.data.repository.IngredientAiRecipeRepository
import com.example.mumuk.data.repository.IngredientRepository
import com.example.mumuk.databinding.FragmentIngredientRecommendBinding
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class IngredientRecommendFragment : Fragment() {
    private var _binding: FragmentIngredientRecommendBinding? = null
    private val binding get() = _binding!!

    private var aiRecipeList: List<Recipe> = emptyList()
    private lateinit var aiRecipeAdapter: IngredientAiRecipeAdapter
    private var isExpanded = false

    private val ingredientRepository by lazy { IngredientRepository(requireContext()) }
    private val aiRecipeRepository = IngredientAiRecipeRepository()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentIngredientRecommendBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 비동기로 데이터 받아오기
        viewLifecycleOwner.lifecycleScope.launch {
            // 1. 재료 리스트
            val ingredientList = ingredientRepository.getIngredients()
            val adapter = IngredientCountAdapter(ingredientList)
            binding.countRV.layoutManager = LinearLayoutManager(requireContext())
            binding.countRV.adapter = adapter

            // 2. AI 추천 레시피 리스트
            aiRecipeList = aiRecipeRepository.getAiRecipes()
            aiRecipeAdapter = IngredientAiRecipeAdapter(emptyList()) { recipe ->
                findNavController().navigate(R.id.action_ingredientRecommendFragment_to_recipeFragment)
            }
            binding.aiRecipeRV.layoutManager = GridLayoutManager(requireContext(), 2)
            binding.aiRecipeRV.adapter = aiRecipeAdapter

            updateAiRecipeList()
        }

        binding.plusBtn.setOnClickListener {
            isExpanded = true
            updateAiRecipeList()
        }

        binding.backBtn.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun updateAiRecipeList() {
        if (!this::aiRecipeAdapter.isInitialized) return
        if (aiRecipeList.size > 6 && !isExpanded) {
            aiRecipeAdapter.updateList(aiRecipeList.take(6))
            binding.plusBtn.visibility = View.VISIBLE
        } else {
            aiRecipeAdapter.updateList(aiRecipeList)
            binding.plusBtn.visibility = View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}