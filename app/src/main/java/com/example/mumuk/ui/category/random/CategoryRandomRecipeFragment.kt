package com.example.mumuk.ui.category.random

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.mumuk.ui.category.CategoryRecipeCard
import com.example.mumuk.ui.category.CategoryRecipeCardAdapter
import com.example.mumuk.databinding.FragmentCategoryRandomRecipeBinding
import com.google.android.material.tabs.TabLayout

class CategoryRandomRecipeFragment : Fragment() {

    private var _binding: FragmentCategoryRandomRecipeBinding? = null
    private val binding get() = _binding!!

    private var selectedTabTitle: String? = null

    companion object {
        fun newInstance(selectedTab: String): CategoryRandomRecipeFragment {
            val fragment = CategoryRandomRecipeFragment()
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
        _binding = FragmentCategoryRandomRecipeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.categoryBackBtn.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        binding.categoryRecipeRecyclerView.layoutManager = GridLayoutManager(context, 2)

        setupSingleTab()
        setupTabListener()
    }

    private fun setupSingleTab() {
        val tabName = selectedTabTitle ?: "랜덤식단"
        val tab = binding.categoryTabLayout.newTab()
        binding.categoryTabLayout.addTab(tab)
        tab.customView = createCustomTabView(tabName, true)
        tab.select()
        updateRecyclerWith(tabName)
    }

    private fun createCustomTabView(title: String, selected: Boolean): View {
        val view = layoutInflater.inflate(com.example.mumuk.R.layout.category_custom_tab, null)
        val textView = view.findViewById<TextView>(com.example.mumuk.R.id.tab_text)
        textView.text = title
        textView.isSelected = selected
        return view
    }

    private fun setupTabListener() {
        binding.categoryTabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.customView?.findViewById<TextView>(com.example.mumuk.R.id.tab_text)?.isSelected = true
                updateRecyclerWith(tab?.customView?.findViewById<TextView>(com.example.mumuk.R.id.tab_text)?.text.toString())
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                tab?.customView?.findViewById<TextView>(com.example.mumuk.R.id.tab_text)?.isSelected = false
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun updateRecyclerWith(tabName: String) {
        val items = when (tabName) {
            "랜덤식단" -> listOf(
                CategoryRecipeCard("랜덤식단", "연어 포케"),
                CategoryRecipeCard("랜덤식단", "연어 포케"),
                CategoryRecipeCard("랜덤식단", "연어 포케")
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