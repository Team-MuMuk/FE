package com.example.mumuk.ui.recommend

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.GridLayoutManager
import com.example.mumuk.data.repository.AiRecipeRepository
import com.example.mumuk.data.repository.IngredientRepository
import com.example.mumuk.databinding.FragmentIngredientRecommendBinding

class IngredientRecommendFragment : Fragment() {
    private var _binding: FragmentIngredientRecommendBinding? = null
    private val binding get() = _binding!!

    // 전체 레시피 리스트를 멤버로 보관
    private lateinit var aiRecipeList: List<com.example.mumuk.data.model.Recipe>
    private lateinit var aiRecipeAdapter: AiRecipeAdapter
    private var isExpanded = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentIngredientRecommendBinding.inflate(inflater, container, false)

        val ingredientList = IngredientRepository().getIngredients()
        val adapter = IngredientCountAdapter(ingredientList)
        binding.countRV.layoutManager = LinearLayoutManager(requireContext())
        binding.countRV.adapter = adapter

        aiRecipeList = AiRecipeRepository().getAiRecipes()
        aiRecipeAdapter = AiRecipeAdapter(emptyList()) // 먼저 비어있는 리스트로 초기화

        binding.aiRecipeRV.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.aiRecipeRV.adapter = aiRecipeAdapter

        updateAiRecipeList()

        binding.plusBtn.setOnClickListener {
            isExpanded = true
            updateAiRecipeList()
        }

        binding.backBtn.setOnClickListener {
            findNavController().popBackStack()
        }

        return binding.root
    }

    private fun updateAiRecipeList() {
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