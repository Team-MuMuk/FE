package com.example.mumuk.ui.category.health

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
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
                Recipe(
                    imageResId = R.drawable.img_food_sample,
                    title = "연어 포케",
                    subtitle = "신선한 연어와 채소가 듬뿍!",
                    isLiked = false
                ),
                Recipe(
                    imageResId = R.drawable.img_food_sample,
                    title = "두부유부초밥",
                    subtitle = "부드럽고 고소하게",
                    isLiked = true
                )
            )
            "혈압관리" -> listOf(
                Recipe(
                    imageResId = R.drawable.img_food_sample,
                    title = "닭가슴살 샐러드",
                    subtitle = "담백한 단백질 샐러드",
                    isLiked = false
                ),
                Recipe(
                    imageResId = R.drawable.img_food_sample,
                    title = "오트밀죽",
                    subtitle = "부드러운 한끼",
                    isLiked = false
                )
            )
            "콜레스테롤 관리" -> listOf(
                Recipe(
                    imageResId = R.drawable.img_food_sample,
                    title = "아보카도 샐러드",
                    subtitle = "건강한 지방 가득",
                    isLiked = false
                ),
                Recipe(
                    imageResId = R.drawable.img_food_sample,
                    title = "병아리콩스튜",
                    subtitle = "식이섬유로 콜레스테롤 관리",
                    isLiked = false
                )
            )
            "소화 건강" -> listOf(
                Recipe(
                    imageResId = R.drawable.img_food_sample,
                    title = "요거트볼",
                    subtitle = "유산균 가득 요거트",
                    isLiked = false
                ),
                Recipe(
                    imageResId = R.drawable.img_food_sample,
                    title = "바나나 오트밀",
                    subtitle = "소화를 돕는 바나나와 오트밀",
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