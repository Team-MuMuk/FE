package com.example.mumuk.ui.category.weight

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.mumuk.ui.category.CategoryItem
import com.example.mumuk.ui.category.CategoryItemAdapter
import com.example.mumuk.databinding.FragmentCategoryWeightBinding
import com.example.mumuk.R

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

        binding.topMessageTv.text = "체형/체중 관리 식단"
        binding.topMessageIcon.setImageResource(R.drawable.ic_category_weight)

        binding.recyclerView.layoutManager = GridLayoutManager(context, 2)
        binding.recyclerView.adapter = CategoryItemAdapter(getDummyList()) { item ->
            val bundle = Bundle().apply {
                putString("selected_tab", item.name)
            }
            findNavController().navigate(
                R.id.action_categoryFragment_to_categoryWeightLossFragment,
                bundle
            )
        }
    }

    private fun getDummyList(): List<CategoryItem> {
        return listOf(
            CategoryItem("체중 감량", R.drawable.ic_category_weight_1),
            CategoryItem("근육 증가", R.drawable.ic_category_weight_2)
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}