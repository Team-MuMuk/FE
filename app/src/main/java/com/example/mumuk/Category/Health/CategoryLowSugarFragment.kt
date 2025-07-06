package com.example.mumuk.Category.Health

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mumuk.Category.CategoryRecipeCard
import com.example.mumuk.Category.CategoryRecipeCardAdapter
import com.example.mumuk.R
import com.google.android.material.tabs.TabLayout

class CategoryLowSugarFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var tabLayout: TabLayout
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
    ): View? = inflater.inflate(R.layout.fragment_category_low_sugar, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<View>(R.id.category_back_btn)?.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        tabLayout = view.findViewById(R.id.category_tab_layout)
        recyclerView = view.findViewById(R.id.category_recipe_recycler_view)
        recyclerView.layoutManager = GridLayoutManager(context, 2)

        setupCustomTabs()
        setupTabSelectionListener()
    }

    private fun setupCustomTabs() {
        val tabs = listOf("당 줄이기", "혈압관리", "콜레스테롤 관리", "소화 건강")
        val initialTab = selectedTabTitle ?: "당 줄이기"

        for (title in tabs) {
            val tab = tabLayout.newTab()
            tabLayout.addTab(tab)
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
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
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

        recyclerView.adapter = CategoryRecipeCardAdapter(items)
    }
}
