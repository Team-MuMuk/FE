package com.example.mumuk.ui.category.health

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.mumuk.ui.category.CategoryRecipeCard
import com.example.mumuk.ui.category.CategoryRecipeCardAdapter
import com.example.mumuk.databinding.FragmentCategoryLowSugarBinding
import com.google.android.material.tabs.TabLayout

class CategoryLowSugarFragment : Fragment() {

    private var _binding: FragmentCategoryLowSugarBinding? = null
    private val binding get() = _binding!!

    private var selectedTabTitle: String? = null

    companion object {
        fun newInstance(selectedTab: String): CategoryLowSugarFragment {
            val fragment = CategoryLowSugarFragment()
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
        _binding = FragmentCategoryLowSugarBinding.inflate(inflater, container, false)
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
        val tabs = listOf("당 줄이기", "혈압관리", "콜레스테롤 관리", "소화 건강")
        val initialTab = selectedTabTitle ?: "당 줄이기"

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
        val view = layoutInflater.inflate(com.example.mumuk.R.layout.category_custom_tab, null)
        val textView = view.findViewById<TextView>(com.example.mumuk.R.id.tab_text)
        textView.text = title
        textView.isSelected = selected
        return view
    }

    private fun setupTabSelectionListener() {
        binding.categoryTabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                val textView = tab?.customView?.findViewById<TextView>(com.example.mumuk.R.id.tab_text)
                textView?.isSelected = true
                updateRecyclerWith(textView?.text.toString())
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                tab?.customView?.findViewById<TextView>(com.example.mumuk.R.id.tab_text)?.isSelected = false
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun updateRecyclerWith(tabName: String) {
        val items = when (tabName) {
            "당 줄이기" -> listOf(
                CategoryRecipeCard("당줄이기", "연어 포케"),
                CategoryRecipeCard("당줄이기", "연어 포케")
            )
            "혈압관리" -> listOf(
                CategoryRecipeCard("혈압관리", "연어 포케"),
                CategoryRecipeCard("혈압관리", "연어 포케")
            )
            "콜레스테롤 관리" -> listOf(
                CategoryRecipeCard("콜레스테롤", "연어 포케"),
                CategoryRecipeCard("콜레스테롤", "연어 포케")
            )
            "소화 건강" -> listOf(
                CategoryRecipeCard("소화건강", "연어 포케"),
                CategoryRecipeCard("소화건강", "연어 포케")
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