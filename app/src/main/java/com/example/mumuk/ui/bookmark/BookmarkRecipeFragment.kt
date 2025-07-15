package com.example.mumuk.ui.bookmark

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.mumuk.R
import com.example.mumuk.databinding.FragmentBookmarkRecipeBinding

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

        recipeAdapter.onItemClick = {
            findNavController().navigate(R.id.action_bookmarkRecipeFragment_to_recipeFragment)
        }

        binding.recipeRV.apply {
            adapter = recipeAdapter
            layoutManager = GridLayoutManager(context, 2)
        }

        bookmarkViewModel.recipes.observe(viewLifecycleOwner) { recipes ->
            recipeAdapter.submitList(recipes)
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}