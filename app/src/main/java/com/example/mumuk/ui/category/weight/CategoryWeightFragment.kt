package com.example.mumuk.ui.category.weight

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.mumuk.ui.category.CategoryFragment
import com.example.mumuk.ui.category.CategoryItem
import com.example.mumuk.ui.category.CategoryItemAdapter
import com.example.mumuk.databinding.FragmentCategoryWeightBinding

class CategoryWeightFragment : Fragment() {
    private var _binding: FragmentCategoryWeightBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCategoryWeightBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerView.layoutManager = GridLayoutManager(context, 2)
        binding.recyclerView.adapter = CategoryItemAdapter(getDummyList()) { item ->
            val selectedTab = item.name
            (parentFragment as? CategoryFragment)?.showFullScreenFragment(
                CategoryWeightLossFragment.newInstance(selectedTab)
            )
        }
    }

    private fun getDummyList(): List<CategoryItem> {
        return listOf(
            CategoryItem("체중 감량", com.example.mumuk.R.drawable.ic_category_weight_1),
            CategoryItem("근육 증가", com.example.mumuk.R.drawable.ic_category_weight_2)
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}