package com.example.mumuk.ui.category.random

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.mumuk.R
import com.example.mumuk.data.api.RetrofitClient
import com.example.mumuk.data.model.Recipe
import com.example.mumuk.databinding.FragmentCategoryRandomRecipeBinding
import com.example.mumuk.ui.category.CategoryRecipeCardAdapter
import com.google.android.material.tabs.TabLayout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CategoryRandomRecipeFragment : Fragment() {

    private var _binding: FragmentCategoryRandomRecipeBinding? = null
    private val binding get() = _binding!!

    private var selectedTabTitle: String? = null

    private var randomRecipeList: MutableList<Recipe>? = null

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
        fetchRandomRecipes()
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
                val tabName = tab?.customView?.findViewById<TextView>(R.id.tab_text)?.text.toString()
                tab?.customView?.findViewById<TextView>(R.id.tab_text)?.isSelected = true
                if (tabName == "랜덤식단") {
                    fetchRandomRecipes(forceRefresh = true)
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                tab?.customView?.findViewById<TextView>(R.id.tab_text)?.isSelected = false
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }
        })
    }

    private fun fetchRandomRecipes(forceRefresh: Boolean = false) {
        if (!forceRefresh && randomRecipeList != null) {
            updateRecyclerWith(randomRecipeList!!)
            return
        }

        val api = RetrofitClient.getRandomRecipeApi(requireContext())
        api.getRandomRecipes().enqueue(object : Callback<com.example.mumuk.data.model.category.RandomRecipeResponse> {
            override fun onResponse(
                call: Call<com.example.mumuk.data.model.category.RandomRecipeResponse>,
                response: Response<com.example.mumuk.data.model.category.RandomRecipeResponse>
            ) {
                if (response.isSuccessful && response.body()?.data != null) {
                    val items = response.body()!!.data.map {
                        Recipe(
                            id = it.recipeId,
                            img = null,
                            title = it.name ?: "알 수 없음",
                            isLiked = it.liked,
                            recipeImageUrl = it.imageUrl
                        )
                    }.toMutableList()
                    randomRecipeList = items
                    updateRecyclerWith(items)
                } else {
                    Toast.makeText(context, "레시피 불러오기 실패", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(
                call: Call<com.example.mumuk.data.model.category.RandomRecipeResponse>,
                t: Throwable
            ) {
                Toast.makeText(context, "네트워크 오류: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun updateRecyclerWith(items: MutableList<Recipe>) {
        binding.categoryRecipeRecyclerView.adapter =
            CategoryRecipeCardAdapter(items) { recipe ->
                val bundle = bundleOf("recipeId" to recipe.id)
                findNavController().navigate(
                    R.id.action_categoryRandomRecipeFragment_to_recipeFragment,
                    bundle
                )
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}