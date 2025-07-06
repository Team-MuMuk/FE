package com.example.mumuk.Category.Random

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

class CategoryRandomRecipeFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var tabLayout: TabLayout
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
    ): View? = inflater.inflate(R.layout.fragment_category_random_recipe, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<View>(R.id.category_back_btn)?.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        tabLayout = view.findViewById(R.id.category_tab_layout)
        recyclerView = view.findViewById(R.id.category_recipe_recycler_view)
        recyclerView.layoutManager = GridLayoutManager(context, 2)

        setupSingleTab()
        setupTabListener()
    }

    private fun setupSingleTab() {
        val tabName = selectedTabTitle ?: "랜덤식단"
        val tab = tabLayout.newTab()
        tabLayout.addTab(tab)
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
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
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
                CategoryRecipeCard("랜덤식단", "연어 포케"),
                CategoryRecipeCard("랜덤식단", "연어 포케"),
                CategoryRecipeCard("랜덤식단", "연어 포케")
            )
            else -> emptyList()
        }

        recyclerView.adapter = CategoryRecipeCardAdapter(items)
    }
}
