package com.example.mumuk.ui.recommend

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.mumuk.R
import com.example.mumuk.databinding.FragmentHealthRecommendBinding
import com.example.mumuk.data.repository.HealthAiRepository

class HealthRecommendFragment : Fragment() {
    private var _binding: FragmentHealthRecommendBinding? = null
    private val binding get() = _binding!!

    private lateinit var aiRecipeList: List<com.example.mumuk.data.model.Recipe>
    private lateinit var aiRecipeAdapter: HealthAiAdapter
    private var isExpanded = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHealthRecommendBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.backBtn.setOnClickListener {
            findNavController().popBackStack()
        }

        aiRecipeList = HealthAiRepository().getAiRecipes()
        aiRecipeAdapter = HealthAiAdapter(emptyList()) { recipe ->
            findNavController().navigate(R.id.action_healthRecommendFragment_to_recipeFragment)
        }

        binding.aiRecipeRV.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.aiRecipeRV.adapter = aiRecipeAdapter

        updateAiRecipeList()

        binding.plusBtn.setOnClickListener {
            isExpanded = true
            updateAiRecipeList()
        }
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