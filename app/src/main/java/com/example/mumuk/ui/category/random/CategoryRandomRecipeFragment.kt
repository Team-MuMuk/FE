package com.example.mumuk.ui.category.random

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.mumuk.R
import com.example.mumuk.data.model.Recipe
import com.example.mumuk.ui.category.CategoryRecipeCardAdapter
import com.example.mumuk.databinding.FragmentCategoryRandomRecipeBinding
import com.google.android.material.tabs.TabLayout

class CategoryRandomRecipeFragment : Fragment() {

    private var _binding: FragmentCategoryRandomRecipeBinding? = null
    private val binding get() = _binding!!

    private var selectedTabTitle: String? = null

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
            findNavController().navigateUp()
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
        val view = layoutInflater.inflate(R.layout.category_custom_tab, null)
        val textView = view.findViewById<TextView>(R.id.tab_text)
        textView.text = title
        textView.isSelected = selected
        return view
    }

    private fun setupTabListener() {
        binding.categoryTabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.customView?.findViewById<TextView>(R.id.tab_text)?.isSelected = true
                updateRecyclerWith(tab?.customView?.findViewById<TextView>(R.id.tab_text)?.text.toString())
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                tab?.customView?.findViewById<TextView>(R.id.tab_text)?.isSelected = false
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun updateRecyclerWith(tabName: String) {
        val items = when (tabName) {
            "랜덤식단" -> listOf(
                Recipe(
                    img = R.drawable.img_food_sample,
                    title = "연어 포케",
                    isLiked = false
                ),
                Recipe(
                    img = R.drawable.img_food_sample,
                    title = "바질 파스타",
                    isLiked = false
                ),
                Recipe(
                    img = R.drawable.img_food_sample,
                    title = "두부유부초밥",
                    isLiked = false
                )
            )
            else -> emptyList()
        }

        binding.categoryRecipeRecyclerView.adapter = CategoryRecipeCardAdapter(items) { recipe ->
            val bundle = Bundle().apply {
                putString("title", recipe.title)
                putInt("img", recipe.img ?: 0)
                putBoolean("isLiked", recipe.isLiked)
            }
            findNavController().navigate(R.id.action_categoryRandomRecipeFragment_to_recipeFragment, bundle)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}