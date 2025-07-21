package com.example.mumuk.ui.category.random

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.mumuk.ui.category.CategoryItem
import com.example.mumuk.ui.category.CategoryItemAdapter
import com.example.mumuk.databinding.FragmentCategoryRandomBinding
import com.example.mumuk.R

class CategoryRandomFragment : Fragment() {
    private var _binding: FragmentCategoryRandomBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCategoryRandomBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerView.layoutManager = GridLayoutManager(context, 2)
        binding.recyclerView.adapter = CategoryItemAdapter(getDummyList()) { item ->
            val bundle = Bundle().apply {
                putString("selected_tab", item.name)
            }
            findNavController().navigate(
                R.id.action_categoryFragment_to_categoryRandomRecipeFragment,
                bundle
            )
        }
    }

    private fun getDummyList(): List<CategoryItem> {
        return listOf(
            CategoryItem("랜덤식단", R.drawable.ic_category_random_1)
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}