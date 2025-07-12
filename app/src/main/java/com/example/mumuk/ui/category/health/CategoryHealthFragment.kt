package com.example.mumuk.ui.category.health

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.mumuk.ui.category.CategoryFragment
import com.example.mumuk.ui.category.CategoryItem
import com.example.mumuk.ui.category.CategoryItemAdapter
import com.example.mumuk.databinding.FragmentCategoryHealthBinding

class CategoryHealthFragment : Fragment() {
    private var _binding: FragmentCategoryHealthBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCategoryHealthBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerView.layoutManager = GridLayoutManager(context, 2)
        binding.recyclerView.adapter = CategoryItemAdapter(getDummyList()) { item ->
            val selectedTab = item.name
            (parentFragment as? CategoryFragment)?.showFullScreenFragment(
                CategoryLowSugarFragment.newInstance(selectedTab)
            )
        }
    }

    private fun getDummyList(): List<CategoryItem> {
        return listOf(
            CategoryItem("당 줄이기", com.example.mumuk.R.drawable.ic_category_health_1),
            CategoryItem("혈압관리", com.example.mumuk.R.drawable.ic_category_health_2),
            CategoryItem("콜레스테롤 관리", com.example.mumuk.R.drawable.ic_category_health_3),
            CategoryItem("소화 건강", com.example.mumuk.R.drawable.ic_category_health_4)
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}