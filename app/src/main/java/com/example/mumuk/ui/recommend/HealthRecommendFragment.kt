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
import com.example.mumuk.data.model.Recipe

class HealthRecommendFragment : Fragment() {
    private var _binding: FragmentHealthRecommendBinding? = null
    private val binding get() = _binding!!

    private lateinit var aiRecipeList: List<Recipe>
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
        aiRecipeAdapter = HealthAiAdapter(
            emptyList(),
            onItemClick = { recipe ->
                findNavController().navigate(R.id.action_healthRecommendFragment_to_recipeFragment)
            },
            onHeartClick = { recipe, position ->
                // 여기에서 찜 상태가 변경될 때 필요한 동작 추가 가능
                // 예: 서버에 찜 상태 저장, Toast 등 (현재는 아무것도 하지 않음)
            }
        )

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