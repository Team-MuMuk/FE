package com.example.mumuk.ui.category.health

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
import com.example.mumuk.databinding.FragmentCategoryLowSugarBinding
import com.example.mumuk.ui.category.CategoryRecipeCardAdapter
import com.google.android.material.tabs.TabLayout

class CategoryLowSugarFragment : Fragment() {

    private var _binding: FragmentCategoryLowSugarBinding? = null
    private val binding get() = _binding!!

    private var selectedTabTitle: String? = null

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
            findNavController().navigateUp()
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
            "당 줄이기" -> mutableListOf(
                Recipe(
                    id = 1,
                    title = "연어 포케",
                    img = R.drawable.img_food_sample,
                    isLiked = false
                ),
                Recipe(
                    id = 2,
                    title = "두부유부초밥",
                    img = R.drawable.img_food_sample,
                    isLiked = true
                )
            )
            "혈압관리" -> mutableListOf(
                Recipe(
                    id = 3,
                    title = "닭가슴살 샐러드",
                    img = R.drawable.img_food_sample,
                    isLiked = false
                ),
                Recipe(
                    id = 4,
                    title = "오트밀죽",
                    img = R.drawable.img_food_sample,
                    isLiked = false
                )
            )
            "콜레스테롤 관리" -> mutableListOf(
                Recipe(
                    id = 5,
                    title = "아보카도 샐러드",
                    img = R.drawable.bg_mosaic,
                    isLiked = false
                ),
                Recipe(
                    id = 6,
                    title = "병아리콩스튜",
                    img = R.drawable.bg_mosaic,
                    isLiked = false
                )
            )
            "소화 건강" -> mutableListOf(
                Recipe(
                    id = 7,
                    title = "요거트볼",
                    img = R.drawable.bg_mosaic,
                    isLiked = false
                ),
                Recipe(
                    id = 8,
                    title = "바나나 오트밀",
                    img = R.drawable.bg_mosaic,
                    isLiked = false
                )
            )
            else -> mutableListOf()
        }

        binding.categoryRecipeRecyclerView.adapter = CategoryRecipeCardAdapter(items) { recipe ->
            val bundle = Bundle().apply {
                putLong("id", recipe.id)
                putString("title", recipe.title)
                putInt("img", recipe.img ?: 0)
                putBoolean("isLiked", recipe.isLiked)
            }
            findNavController().navigate(R.id.action_categoryLowSugarFragment_to_recipeFragment, bundle)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}