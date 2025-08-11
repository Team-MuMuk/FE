package com.example.mumuk.ui.bookmark

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.mumuk.R
import com.example.mumuk.databinding.FragmentBookmarkRecipeBinding
import com.google.android.material.button.MaterialButton

class BookmarkRecipeFragment : Fragment() {

    private var _binding: FragmentBookmarkRecipeBinding? = null
    private val binding get() = _binding!!
    private val bookmarkViewModel: BookmarkRecipeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBookmarkRecipeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.backBtn.setOnClickListener {
            findNavController().navigateUp()
        }

        val recipeAdapter = BookmarkRecipeAdapter()

        recipeAdapter.onItemClick = { recipe ->
            Log.d("BookmarkFragment", "Recipe clicked. ID: ${recipe.id}")
            val bundle = bundleOf("recipeId" to recipe.id)
            findNavController().navigate(R.id.action_bookmarkRecipeFragment_to_recipeFragment, bundle)
        }

        recipeAdapter.onHeartClick = { recipe, position ->
            // 좋아요 버튼 클릭 로직
        }

        binding.recipeRV.apply {
            adapter = recipeAdapter
            layoutManager = GridLayoutManager(context, 2)
        }

        bookmarkViewModel.recipes.observe(viewLifecycleOwner) { recipes ->
            recipeAdapter.submitList(recipes)
        }

        // 버튼 색상 리소스
        val buttons = listOf(binding.button, binding.button2, binding.button3)
        val selectedBg = ContextCompat.getColor(requireContext(), R.color.green_100)
        val selectedText = ContextCompat.getColor(requireContext(), R.color.green_800)
        val unselectedBg = ContextCompat.getColor(requireContext(), R.color.black_100)
        val unselectedText = ContextCompat.getColor(requireContext(), R.color.black)

        fun selectButton(selected: MaterialButton) {
            buttons.forEach { button ->
                if (button == selected) {
                    button.setBackgroundColor(selectedBg)
                    button.setTextColor(selectedText)
                } else {
                    button.setBackgroundColor(unselectedBg)
                    button.setTextColor(unselectedText)
                }
            }
        }

        selectButton(binding.button)
        bookmarkViewModel.loadRecipes(RecipeCategory.WEIGHT)


        binding.button.setOnClickListener {
            selectButton(binding.button)
            bookmarkViewModel.loadRecipes(RecipeCategory.WEIGHT)
        }
        binding.button2.setOnClickListener {
            selectButton(binding.button2)
            bookmarkViewModel.loadRecipes(RecipeCategory.HEALTH)
        }
        binding.button3.setOnClickListener {
            selectButton(binding.button3)
            bookmarkViewModel.loadRecipes(RecipeCategory.RANDOM)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}