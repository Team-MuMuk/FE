package com.example.mumuk.ui.search.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mumuk.R
import com.example.mumuk.data.api.RetrofitClient
import com.example.mumuk.data.model.search.RecentSearch
import com.example.mumuk.data.model.search.RecentSearchResponse
import com.example.mumuk.data.model.search.PopularKeywordResponse
import com.example.mumuk.databinding.FragmentSearchBinding
import com.example.mumuk.databinding.ItemSearchSuggestKeywordChipBinding
import com.example.mumuk.data.model.Recipe
import com.example.mumuk.ui.search.SearchRecentRecipeAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.util.Log

class SearchFragment : Fragment() {
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private val recentKeywords = mutableListOf<RecentSearch>()
    private lateinit var recentKeywordAdapter: SearchRecentKeywordAdapter

    private val suggestKeywords = listOf("포케", "아보카도 샐러드", "샐러드", "닭가슴살", "건강주스", "키토김밥")
    private var popularKeywords = listOf<String>()

    private val recentRecipes = listOf(
        Recipe(
            img = R.drawable.img_food_sample,
            title = "연어 포케",
            isLiked = false
        ),
        Recipe(
            img = R.drawable.img_food_sample,
            title = "바스크치즈케이크",
            isLiked = false
        )
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)

        setupRecentKeywordList()
        setupSuggestKeywordChips(inflater)
        fetchPopularKeywordsFromApi()
        setupRecentRecipeList()
        fetchRecentKeywordsFromApi()

        binding.searchEditEt.setOnClickListener {
            findNavController().navigate(R.id.action_searchFragment_to_searchAutocompleteFragment)
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        fetchRecentKeywordsFromApi()
        fetchPopularKeywordsFromApi()
    }

    private fun setupRecentKeywordList() {
        recentKeywordAdapter = SearchRecentKeywordAdapter(
            recentKeywords,
            onKeywordClick = { keyword ->
                binding.searchEditEt.setText(keyword)
            },
            onDeleteClick = { item, position ->
                deleteRecentKeyword(item.title ?: "", item.createdAt)
                recentKeywordAdapter.removeAt(position)
                if (recentKeywords.isEmpty()) {
                    setRecentKeywordEmptyView(true)
                }
            }
        )
        binding.searchRecentKeywordsRv.adapter = recentKeywordAdapter
        binding.searchRecentKeywordsRv.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
    }

    private fun fetchRecentKeywordsFromApi() {
        val context = context ?: return
        val api = RetrofitClient.getRecentSearchApi(context)
        api.getRecentSearches().enqueue(object : Callback<RecentSearchResponse> {
            override fun onResponse(
                call: Call<RecentSearchResponse>,
                response: Response<RecentSearchResponse>
            ) {
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body?.status == "OK" && body.data != null) {
                        recentKeywords.clear()
                        recentKeywords.addAll(body.data)
                        recentKeywordAdapter.notifyDataSetChanged()
                        setRecentKeywordEmptyView(false)
                    } else if (body?.code == "SEARCH_404") {
                        recentKeywords.clear()
                        recentKeywordAdapter.notifyDataSetChanged()
                        setRecentKeywordEmptyView(true)
                    }
                } else {
                    recentKeywords.clear()
                    recentKeywordAdapter.notifyDataSetChanged()
                    setRecentKeywordEmptyView(true)
                }
            }
            override fun onFailure(call: Call<RecentSearchResponse>, t: Throwable) {
                recentKeywords.clear()
                recentKeywordAdapter.notifyDataSetChanged()
                setRecentKeywordEmptyView(true)
            }
        })
    }

    private fun setRecentKeywordEmptyView(isEmpty: Boolean) {
        val parent = binding.searchRecentKeywordsRv.parent as ViewGroup
        var emptyView = parent.findViewWithTag<TextView>("recent_empty_tv")
        if (isEmpty) {
            binding.searchRecentKeywordsRv.visibility = View.GONE
            if (emptyView == null) {
                emptyView = TextView(requireContext()).apply {
                    text = "최근 검색어가 없습니다."
                    textSize = 14f
                    setTextColor(resources.getColor(android.R.color.darker_gray, null))
                    setPadding(0, 8, 0, 8)
                    tag = "recent_empty_tv"
                }
                parent.addView(emptyView, parent.indexOfChild(binding.searchRecentKeywordsRv))
            } else {
                emptyView.visibility = View.VISIBLE
            }
        } else {
            binding.searchRecentKeywordsRv.visibility = View.VISIBLE
            emptyView?.visibility = View.GONE
        }
    }

    fun saveRecentKeyword(keyword: String) {
        val context = context ?: return
        val api = RetrofitClient.getRecentSearchApi(context)
        api.saveRecentSearch(keyword).enqueue(object : Callback<RecentSearchResponse> {
            override fun onResponse(
                call: Call<RecentSearchResponse>,
                response: Response<RecentSearchResponse>
            ) {
            }
            override fun onFailure(call: Call<RecentSearchResponse>, t: Throwable) {
            }
        })
    }

    fun deleteRecentKeyword(title: String, createdAt: String?) {
        val context = context ?: return
        val api = RetrofitClient.getRecentSearchApi(context)
        val request = RecentSearch(title, createdAt)
        api.deleteRecentSearch(request).enqueue(object : Callback<RecentSearchResponse> {
            override fun onResponse(
                call: Call<RecentSearchResponse>,
                response: Response<RecentSearchResponse>
            ) {
            }
            override fun onFailure(call: Call<RecentSearchResponse>, t: Throwable) {
            }
        })
    }

    private fun setupSuggestKeywordChips(inflater: LayoutInflater) {
        val flexbox = binding.searchSuggestKeywordsFl
        flexbox.removeAllViews()
        val keywordsPerRow = 3
        val keywords = suggestKeywords.take(6)

        for (i in keywords.indices step keywordsPerRow) {
            val rowLayout = LinearLayout(requireContext()).apply {
                orientation = LinearLayout.HORIZONTAL
            }
            for (j in 0 until keywordsPerRow) {
                val index = i + j
                if (index < keywords.size) {
                    val chipBinding = ItemSearchSuggestKeywordChipBinding.inflate(inflater, rowLayout, false)
                    chipBinding.searchSuggestKeywordTv.text = keywords[index]
                    chipBinding.searchSuggestKeywordTv.setOnClickListener {
                        binding.searchEditEt.setText(keywords[index])
                    }
                    rowLayout.addView(chipBinding.root)
                }
            }
            flexbox.addView(rowLayout)
        }
    }

    private fun fetchPopularKeywordsFromApi() {
        val context = context ?: return
        val api = RetrofitClient.getPopularKeywordApi(context)
        api.getPopularKeywords().enqueue(object : Callback<PopularKeywordResponse> {
            override fun onResponse(
                call: Call<PopularKeywordResponse>,
                response: Response<PopularKeywordResponse>
            ) {
                if (response.isSuccessful) {
                    val body = response.body()
                    Log.d("SearchFragment", "popularKeywords API data: ${body?.data}")
                    if (body?.status == "OK" && body.data != null) {
                        popularKeywords = body.data
                        setupPopularKeywordList()
                    }
                } else {
                    Log.d("SearchFragment", "popularKeywords API failed: ${response.code()} ${response.message()}")
                }
            }
            override fun onFailure(call: Call<PopularKeywordResponse>, t: Throwable) {
                Log.e("SearchFragment", "popularKeywords API error", t)
            }
        })
    }

    private fun setupPopularKeywordList() {
        if (popularKeywords.isEmpty()) {
            binding.searchPopularKeywordsRv.visibility = View.GONE
            binding.popularEmptyTv.visibility = View.VISIBLE
        } else {
            binding.searchPopularKeywordsRv.visibility = View.VISIBLE
            binding.popularEmptyTv.visibility = View.GONE
            val adapter = SearchPopularAdapter(popularKeywords) { keyword ->
                binding.searchEditEt.setText(keyword)
            }
            binding.searchPopularKeywordsRv.adapter = adapter
            binding.searchPopularKeywordsRv.layoutManager = GridLayoutManager(context, 2)
        }
    }

    private fun setupRecentRecipeList() {
        val adapter = SearchRecentRecipeAdapter(recentRecipes) { recipe ->
            val bundle = Bundle().apply {
                putString("title", recipe.title)
                putInt("img", recipe.img ?: 0)
                putBoolean("isLiked", recipe.isLiked)
            }
            findNavController().navigate(R.id.action_searchFragment_to_recipeFragment, bundle)
        }
        binding.searchRecentRecipeRv.adapter = adapter
        binding.searchRecentRecipeRv.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}