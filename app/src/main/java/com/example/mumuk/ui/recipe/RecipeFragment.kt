package com.example.mumuk.ui.recipe

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mumuk.databinding.FragmentRecipeBinding

class RecipeFragment : Fragment() {
    private var _binding: FragmentRecipeBinding? = null
    private val binding get() = _binding!!

    private val recipeViewModel: RecipeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecipeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.backBtn.setOnClickListener {
            findNavController().navigateUp()
        }

        val nutritionInfoAdapter = NutritionInfoAdapter()
        binding.infoRV.apply {
            adapter = nutritionInfoAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        }

        recipeViewModel.nutritionInfoList.observe(viewLifecycleOwner) { infoList ->
            nutritionInfoAdapter.submitList(infoList)
        }

        val shopAdapter = ShopAdapter()
        binding.shopRV.apply {
            adapter = shopAdapter
            layoutManager = GridLayoutManager(context, 2)
        }

        recipeViewModel.shopItemList.observe(viewLifecycleOwner) { shopList ->
            shopAdapter.submitList(shopList)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}