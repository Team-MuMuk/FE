package com.example.mumuk.ui.category.weight

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.mumuk.R
import com.example.mumuk.data.model.Recipe
import com.example.mumuk.ui.category.CategoryRecipeCardAdapter
import com.example.mumuk.databinding.FragmentCategoryWeightLossBinding
import com.google.android.material.tabs.TabLayout

class CategoryWeightLossFragment : Fragment() {

    private var _binding: FragmentCategoryWeightLossBinding? = null
    private val binding get() = _binding!!

    private var selectedTabTitle: String? = null

    companion object {
        fun newInstance(selectedTab: String): CategoryWeightLossFragment {
            val fragment = CategoryWeightLossFragment()
            val args = Bundle()
            args.putString("selected_tab", selectedTab)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        selectedTabTitle = arguments?.getString("selected_tab")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCategoryWeightLossBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.categoryBackBtn.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        binding.categoryRecipeRecyclerView.layoutManager = GridLayoutManager(context, 2)

        setupCustomTabs()
        setupTabSelectionListener()
    }

    private fun setupCustomTabs() {
        val tabs = listOf("체중 감량", "근육 증가")
        val initialTab = selectedTabTitle ?: "체중 감량"

        for (title in tabs) {
            val tab = binding.categoryTabLayout.newTab()
            binding.categoryTabLayout.addTab(tab)
            tab.customView = createCustomTabView(title, selected = (title == initialTab))
            if (title == initialTab) {
                tab.select()
                updateRecyclerWith(title)
            }
        }
    }

    private fun createCustomTabView(title: String, selected: Boolean): View {
        val view = layoutInflater.inflate(R.layout.category_custom_tab, null)
        val textView = view.findViewById<TextView>(R.id.tab_text)
        textView.text = title
        textView.isSelected = selected
        return view
    }

    private fun setupTabSelectionListener() {
        binding.categoryTabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                val textView = tab?.customView?.findViewById<TextView>(R.id.tab_text)
                textView?.isSelected = true
                updateRecyclerWith(textView?.text.toString())
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                tab?.customView?.findViewById<TextView>(R.id.tab_text)?.isSelected = false
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun updateRecyclerWith(tabName: String) {
        val items = when (tabName) {
            "체중 감량" -> listOf(
                Recipe(
                    imageResId = R.drawable.img_food_sample,
                    title = "연어 포케",
                    subtitle = "고단백 저칼로리 식단",
                    isLiked = false
                ),
                Recipe(
                    imageResId = R.drawable.img_food_sample,
                    title = "닭가슴살 샐러드",
                    subtitle = "프레시한 샐러드와 단백질",
                    isLiked = false
                )
            )
            "근육 증가" -> listOf(
                Recipe(
                    imageResId = R.drawable.img_food_sample,
                    title = "닭가슴살 스테이크",
                    subtitle = "벌크업에 좋은 단백질 식단",
                    isLiked = false
                ),
                Recipe(
                    imageResId = R.drawable.img_food_sample,
                    title = "오트밀 스크램블",
                    subtitle = "근육에 좋은 복합탄수화물",
                    isLiked = false
                )
            )
            else -> emptyList()
        }

        binding.categoryRecipeRecyclerView.adapter = CategoryRecipeCardAdapter(items)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}