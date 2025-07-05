package com.example.mumuk.Category

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.mumuk.Category.Health.CategoryHealthFragment
import com.example.mumuk.Category.Random.CategoryRandomFragment
import com.example.mumuk.Category.Weight.CategoryWeightFragment
import com.example.mumuk.R
import com.example.mumuk.databinding.FragmentCategoryBinding

class CategoryFragment : Fragment() {
    private var _binding: FragmentCategoryBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCategoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btns = listOf(
            binding.categoryWeightTypeBtn,
            binding.categoryHealthTypeBtn,
            binding.categoryRandomBtn
        )

        replaceCategoryFragment(CategoryWeightFragment())
        btns.forEachIndexed { i, btn -> btn.isSelected = (i == 0) }

        btns[0].setOnClickListener {
            replaceCategoryFragment(CategoryWeightFragment())
            btns.forEachIndexed { i, b -> b.isSelected = (i == 0) }
        }
        btns[1].setOnClickListener {
            replaceCategoryFragment(CategoryHealthFragment())
            btns.forEachIndexed { i, b -> b.isSelected = (i == 1) }
        }
        btns[2].setOnClickListener {
            replaceCategoryFragment(CategoryRandomFragment())
            btns.forEachIndexed { i, b -> b.isSelected = (i == 2) }
        }
    }

    private fun replaceCategoryFragment(fragment: Fragment) {
        childFragmentManager.beginTransaction()
            .replace(R.id.category_content_container_fl, fragment)
            .commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}