package com.example.mumuk.ui.category

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.mumuk.databinding.FragmentCategoryBinding

class CategoryFragment : Fragment() {
    private var _binding: FragmentCategoryBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CategoryViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCategoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val btns = listOf(
            binding.categoryWeightTypeBtn,
            binding.categoryHealthTypeBtn,
            binding.categoryRandomBtn
        )
        // ViewModel에서 인덱스 복원
        btns.forEachIndexed { i, btn -> btn.isSelected = (i == viewModel.selectedCategoryIdx) }
        showMiddleFragment(viewModel.selectedCategoryIdx)

        binding.categoryWeightTypeBtn.setOnClickListener { selectCategory(0, btns) }
        binding.categoryHealthTypeBtn.setOnClickListener { selectCategory(1, btns) }
        binding.categoryRandomBtn.setOnClickListener { selectCategory(2, btns) }
    }

    private fun selectCategory(idx: Int, btns: List<Button>) {
        viewModel.selectedCategoryIdx = idx
        btns.forEachIndexed { i, btn -> btn.isSelected = (i == idx) }
        showMiddleFragment(idx)
    }

    private fun showMiddleFragment(idx: Int) {
        val fragment = when (idx) {
            0 -> com.example.mumuk.ui.category.weight.CategoryWeightFragment()
            1 -> com.example.mumuk.ui.category.health.CategoryHealthFragment()
            2 -> com.example.mumuk.ui.category.random.CategoryRandomFragment()
            else -> com.example.mumuk.ui.category.weight.CategoryWeightFragment()
        }
        childFragmentManager.beginTransaction()
            .replace(com.example.mumuk.R.id.category_content_container_fl, fragment)
            .commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}