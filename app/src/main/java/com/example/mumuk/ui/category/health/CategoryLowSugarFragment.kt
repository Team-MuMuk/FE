package com.example.mumuk.ui.category.health

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.mumuk.R
import com.example.mumuk.data.api.RetrofitClient
import com.example.mumuk.data.model.category.CategoryRecipeResponse
import com.example.mumuk.data.model.Recipe
import com.example.mumuk.databinding.FragmentCategoryLowSugarBinding
import com.example.mumuk.ui.category.CategoryRecipeCardAdapter
import com.google.android.material.tabs.TabLayout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

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

    private fun getApiCategory(tabName: String): String {
        return when (tabName) {
            "당 줄이기" -> "sugar_reduction"
            "혈압관리" -> "blood_pressure"
            "콜레스테롤 관리" -> "cholesterol"
            "소화 건강" -> "digestion"
            else -> "other"
        }
    }

    private fun updateRecyclerWith(tabName: String) {
        val apiCategory = getApiCategory(tabName)
        val context = requireContext()
        val api = RetrofitClient.getCategoryRecipeApi(context)

        api.getRecommendedRecipes(apiCategory).enqueue(object : Callback<CategoryRecipeResponse> {
            override fun onResponse(
                call: Call<CategoryRecipeResponse>,
                response: Response<CategoryRecipeResponse>
            ) {
                if (response.isSuccessful && response.body()?.data != null) {
                    val recipes = response.body()!!.data.map { categoryRecipe ->
                        Recipe(
                            id = categoryRecipe.recipeId,
                            img = null,
                            title = categoryRecipe.name ?: "",
                            isLiked = categoryRecipe.liked,
                            recipeImageUrl = categoryRecipe.imageUrl
                        )
                    }
                    binding.categoryRecipeRecyclerView.adapter =
                        CategoryRecipeCardAdapter(recipes.toMutableList()) { recipe ->
                            val bundle = bundleOf("recipeId" to recipe.id)
                            findNavController().navigate(R.id.action_categoryLowSugarFragment_to_recipeFragment, bundle)
                        }
                }
            }

            override fun onFailure(call: Call<CategoryRecipeResponse>, t: Throwable) {
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}