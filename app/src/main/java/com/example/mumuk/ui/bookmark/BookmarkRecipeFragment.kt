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
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.mumuk.R
import com.example.mumuk.data.api.RetrofitClient
import com.example.mumuk.data.repository.BookmarkRecipeRepository
import com.example.mumuk.data.repository.RecipeCategory
import com.example.mumuk.databinding.FragmentBookmarkRecipeBinding
import com.google.android.material.button.MaterialButton

class BookmarkRecipeFragment : Fragment() {

    private var _binding: FragmentBookmarkRecipeBinding? = null
    private val binding get() = _binding!!

    private val bookmarkViewModel: BookmarkRecipeViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val repo = BookmarkRecipeRepository(
                    categoryApi = RetrofitClient.getCategoryRecipeApi(requireContext()),
                    userRecipeApiService = RetrofitClient.getUserRecipeApi(requireContext())
                )
                @Suppress("UNCHECKED_CAST")
                return BookmarkRecipeViewModel(repo) as T
            }
        }
    }

    private var currentCategory = RecipeCategory.WEIGHT

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBookmarkRecipeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.backBtn.setOnClickListener { findNavController().navigateUp() }

        val recipeAdapter = BookmarkRecipeAdapter().apply {
            onItemClick = { recipe ->
                val bundle = bundleOf("recipeId" to recipe.id)
                findNavController().navigate(R.id.action_bookmarkRecipeFragment_to_recipeFragment, bundle)
            }

            onHeartClick = { recipe ->
                bookmarkViewModel.onHeartClick(recipe)
            }
        }

        binding.recipeRV.apply {
            layoutManager = GridLayoutManager(context, 2)
            adapter = recipeAdapter
        }

        bookmarkViewModel.recipes.observe(viewLifecycleOwner) { recipes ->
            recipeAdapter.submitList(recipes)
        }

        val buttons = listOf(binding.button, binding.button2, binding.button3)
        val selectedBg = ContextCompat.getColor(requireContext(), R.color.green_100)
        val selectedText = ContextCompat.getColor(requireContext(), R.color.green_800)
        val unselectedBg = ContextCompat.getColor(requireContext(), R.color.black_100)
        val unselectedText = ContextCompat.getColor(requireContext(), R.color.black)

        fun selectButton(selected: MaterialButton) {
            buttons.forEach { btn ->
                if (btn == selected) {
                    btn.setBackgroundColor(selectedBg)
                    btn.setTextColor(selectedText)
                } else {
                    btn.setBackgroundColor(unselectedBg)
                    btn.setTextColor(unselectedText)
                }
            }
        }

        fun selectCategory(cat: RecipeCategory) {
            currentCategory = cat
            when (cat) {
                RecipeCategory.ALL -> selectButton(binding.button)
                RecipeCategory.WEIGHT -> selectButton(binding.button2)
                RecipeCategory.HEALTH -> selectButton(binding.button3)
            }
            bookmarkViewModel.loadRecipes(cat)
        }

        selectCategory(RecipeCategory.ALL)


        binding.button.setOnClickListener { selectCategory(RecipeCategory.ALL) }
        binding.button2.setOnClickListener { selectCategory(RecipeCategory.WEIGHT) }
        binding.button3.setOnClickListener { selectCategory(RecipeCategory.HEALTH) }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
