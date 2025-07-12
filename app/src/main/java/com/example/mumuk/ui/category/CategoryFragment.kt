package com.example.mumuk.ui.category

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.mumuk.ui.category.Health.CategoryHealthFragment
import com.example.mumuk.ui.category.Random.CategoryRandomFragment
import com.example.mumuk.ui.category.Weight.CategoryWeightFragment
import com.example.mumuk.databinding.FragmentCategoryBinding
import com.example.mumuk.R

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

        childFragmentManager.addOnBackStackChangedListener {
            val fullscreenFragment = childFragmentManager.findFragmentById(R.id.category_fullscreen_container)
            if (fullscreenFragment == null) {
                binding.categoryButtonArea.visibility = View.VISIBLE
                binding.categoryContentContainerFl.visibility = View.VISIBLE
                binding.categoryFullscreenContainer.visibility = View.GONE
            }
        }
    }

    private fun replaceCategoryFragment(fragment: Fragment) {
        binding.categoryButtonArea.visibility = View.VISIBLE
        binding.categoryContentContainerFl.visibility = View.VISIBLE
        binding.categoryFullscreenContainer.visibility = View.GONE

        childFragmentManager.beginTransaction()
            .replace(R.id.category_content_container_fl, fragment)
            .commit()
    }

    fun showFullScreenFragment(fragment: Fragment) {
        binding.categoryButtonArea.visibility = View.GONE
        binding.categoryContentContainerFl.visibility = View.GONE
        binding.categoryFullscreenContainer.visibility = View.VISIBLE

        childFragmentManager.beginTransaction()
            .replace(R.id.category_fullscreen_container, fragment)
            .addToBackStack(null)
            .commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
