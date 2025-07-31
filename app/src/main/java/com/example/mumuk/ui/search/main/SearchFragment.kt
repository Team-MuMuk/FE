package com.example.mumuk.ui.search.main

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mumuk.R
import com.example.mumuk.data.api.RetrofitClient
import com.example.mumuk.data.model.search.RecentSearch
import com.example.mumuk.data.model.search.RecentSearchResponse
import com.example.mumuk.data.model.search.PopularKeywordResponse
import com.example.mumuk.data.model.search.SuggestKeywordResponse
import com.example.mumuk.databinding.FragmentSearchBinding
import com.example.mumuk.databinding.ItemSearchSuggestKeywordChipBinding
import com.example.mumuk.ui.search.SearchRecentRecipeViewModel
import com.example.mumuk.data.model.search.RecentRecipe
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class SearchFragment : Fragment() {
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private val recentKeywords = mutableListOf<RecentSearch>()
    private lateinit var recentKeywordAdapter: SearchRecentKeywordAdapter

    private var suggestKeywords = listOf<String>()
    private var popularKeywords = listOf<String>()

    private lateinit var recentRecipeViewModel: SearchRecentRecipeViewModel
    private lateinit var recentRecipeAdapter: SearchRecentRecipeAdapter
    private val recentRecipeList = mutableListOf<RecentRecipe>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)

        setupRecentKeywordList()
        fetchSuggestKeywordsFromApi(inflater)
        fetchPopularKeywordsFromApi()
        fetchRecentKeywordsFromApi()

        binding.searchEditEt.setOnClickListener {
            findNavController().navigate(R.id.action_searchFragment_to_searchAutocompleteFragment)
        }

        val apiService = RetrofitClient.getRecipeApi(requireContext())
        recentRecipeViewModel = ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return SearchRecentRecipeViewModel(apiService) as T
            }
        })[SearchRecentRecipeViewModel::class.java]

        setupRecentRecipeList()
        observeRecentRecipes()
        recentRecipeViewModel.fetchRecentRecipes()

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        fetchRecentKeywordsFromApi()
        fetchPopularKeywordsFromApi()
        recentRecipeViewModel.fetchRecentRecipes()
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
        Log.d("RecentKeyword", "최근 검색어 조회 API 호출 시작")
        api.getRecentSearches().enqueue(object : Callback<RecentSearchResponse> {
            override fun onResponse(
                call: Call<RecentSearchResponse>,
                response: Response<RecentSearchResponse>
            ) {
                Log.d("RecentKeyword", "API 응답 코드: ${response.code()}")
                Log.d("RecentKeyword", "API 응답 body: ${response.body()}")
                Log.d("RecentKeyword", "API 응답 errorBody: ${response.errorBody()?.string()}")
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body?.status == "OK" && body.data != null) {
                        recentKeywords.clear()
                        val keywordList = when (body.data) {
                            is List<*> -> {
                                (body.data as List<*>).mapNotNull { item ->
                                    if (item is RecentSearch) {
                                        item
                                    } else if (item is Map<*, *>) {
                                        val title = item["title"] as? String
                                        val createdAt = item["createdAt"] as? String
                                        if (title != null && createdAt != null) RecentSearch(title, createdAt) else null
                                    } else null
                                }
                            }
                            else -> emptyList()
                        }
                        recentKeywords.addAll(keywordList)
                        recentKeywordAdapter.notifyDataSetChanged()
                        setRecentKeywordEmptyView(keywordList.isEmpty())
                        Log.d("RecentKeyword", "최근 검색어 정상 수신 개수: ${keywordList.size}")
                    } else if (body?.code == "SEARCH_404") {
                        recentKeywords.clear()
                        recentKeywordAdapter.notifyDataSetChanged()
                        setRecentKeywordEmptyView(true)
                        Log.d("RecentKeyword", "최근 검색어 없음(SEARCH_404)")
                    }
                } else {
                    recentKeywords.clear()
                    recentKeywordAdapter.notifyDataSetChanged()
                    setRecentKeywordEmptyView(true)
                    Log.e("RecentKeyword", "최근 검색어 불러오기 실패: ${response.code()}")
                }
            }
            override fun onFailure(call: Call<RecentSearchResponse>, t: Throwable) {
                Log.e("RecentKeyword", "네트워크 오류: ${t.message}", t)
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
        Log.d("RecentKeyword", "최근 검색어 저장 API 호출 시작: $keyword")
        api.saveRecentSearch(keyword).enqueue(object : Callback<RecentSearchResponse> {
            override fun onResponse(
                call: Call<RecentSearchResponse>,
                response: Response<RecentSearchResponse>
            ) {
                Log.d("RecentKeyword", "저장 API 응답 코드: ${response.code()}")
                Log.d("RecentKeyword", "저장 API 응답 body: ${response.body()}")
                Log.d("RecentKeyword", "저장 API 응답 errorBody: ${response.errorBody()?.string()}")
            }
            override fun onFailure(call: Call<RecentSearchResponse>, t: Throwable) {
                Log.e("RecentKeyword", "저장 API 네트워크 오류: ${t.message}", t)
            }
        })
    }

    fun deleteRecentKeyword(title: String, createdAt: String?) {
        val context = context ?: return
        val api = RetrofitClient.getRecentSearchApi(context)
        val request = RecentSearch(title, createdAt)
        Log.d("RecentKeyword", "최근 검색어 삭제 API 호출: title=$title, createdAt=$createdAt")
        api.deleteRecentSearch(request).enqueue(object : Callback<RecentSearchResponse> {
            override fun onResponse(
                call: Call<RecentSearchResponse>,
                response: Response<RecentSearchResponse>
            ) {
                Log.d("RecentKeyword", "삭제 API 응답 코드: ${response.code()}")
                Log.d("RecentKeyword", "삭제 API 응답 body: ${response.body()}")
                Log.d("RecentKeyword", "삭제 API 응답 errorBody: ${response.errorBody()?.string()}")
            }
            override fun onFailure(call: Call<RecentSearchResponse>, t: Throwable) {
                Log.e("RecentKeyword", "삭제 API 네트워크 오류: ${t.message}", t)
            }
        })
    }

    private fun fetchSuggestKeywordsFromApi(inflater: LayoutInflater) {
        val context = context ?: return
        val api = RetrofitClient.getSuggestKeywordApi(context)
        Log.d("SuggestKeyword", "추천 검색어 API 호출 시작")
        api.getSuggestKeywords().enqueue(object : Callback<SuggestKeywordResponse> {
            override fun onResponse(
                call: Call<SuggestKeywordResponse>,
                response: Response<SuggestKeywordResponse>
            ) {
                Log.d("SuggestKeyword", "API 응답 코드: ${response.code()}")
                Log.d("SuggestKeyword", "API 응답 body: ${response.body()}")
                Log.d("SuggestKeyword", "API 응답 errorBody: ${response.errorBody()?.string()}")
                val body = response.body()
                val keywords = body?.data ?: emptyList()
                suggestKeywords = keywords
                setupSuggestKeywordChips(inflater)
                Log.d("SuggestKeyword", "추천 검색어 개수: ${suggestKeywords.size}")
            }
            override fun onFailure(call: Call<SuggestKeywordResponse>, t: Throwable) {
                Log.e("SuggestKeyword", "API 호출 실패: ${t.message}", t)
                suggestKeywords = emptyList()
                setupSuggestKeywordChips(inflater)
            }
        })
    }

    private fun setupSuggestKeywordChips(inflater: LayoutInflater) {
        val flexbox = binding.searchSuggestKeywordsFl
        flexbox.removeAllViews()
        val keywordsPerRow = 3
        val keywords = suggestKeywords.take(6)

        if (keywords.isEmpty()) {
            Log.d("SuggestKeyword", "추천 검색어 없음 안내 표시")
            val emptyTv = TextView(requireContext()).apply {
                text = "추천 검색어가 없습니다."
                textSize = 14f
                setTextColor(resources.getColor(android.R.color.darker_gray, null))
                setPadding(0, 8, 0, 8)
            }
            flexbox.addView(emptyTv)
            return
        }

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
        Log.d("SuggestKeyword", "추천 검색어 칩 UI 세팅 완료. 칩 개수: ${keywords.size}")
    }

    private fun fetchPopularKeywordsFromApi() {
        val context = context ?: return
        val api = RetrofitClient.getPopularKeywordApi(context)
        Log.d("PopularKeyword", "인기 검색어 API 호출 시작")
        api.getPopularKeywords().enqueue(object : Callback<PopularKeywordResponse> {
            override fun onResponse(
                call: Call<PopularKeywordResponse>,
                response: Response<PopularKeywordResponse>
            ) {
                Log.d("PopularKeyword", "API 응답 코드: ${response.code()}")
                Log.d("PopularKeyword", "API 응답 body: ${response.body()}")
                Log.d("PopularKeyword", "API 응답 errorBody: ${response.errorBody()?.string()}")
                val body = response.body()
                val keywords = body?.data?.trendKeywordList
                val timeRaw = body?.data?.localDateTime

                popularKeywords = keywords ?: emptyList()
                setupPopularKeywordList()

                if (!timeRaw.isNullOrBlank() && !popularKeywords.isNullOrEmpty()) {
                    val displayTime = formatPopularTime(timeRaw)
                    binding.searchPopularTimeTv.text = "$displayTime 기준"
                } else {
                    binding.searchPopularTimeTv.text = "없음"
                }
                Log.d("PopularKeyword", "인기 검색어 개수: ${popularKeywords.size}")
            }
            override fun onFailure(call: Call<PopularKeywordResponse>, t: Throwable) {
                Log.e("PopularKeyword", "API 호출 실패: ${t.message}", t)
                popularKeywords = emptyList()
                setupPopularKeywordList()
                binding.searchPopularTimeTv.text = "없음"
            }
        })
    }

    private fun formatPopularTime(localDateTime: String): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            inputFormat.timeZone = TimeZone.getTimeZone("UTC")
            val date = inputFormat.parse(localDateTime)
            val outputFormat = SimpleDateFormat("yyyy.MM.dd HH:mm", Locale.getDefault())
            outputFormat.format(date!!)
        } catch (e: Exception) {
            ""
        }
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
        recentRecipeAdapter = SearchRecentRecipeAdapter(recentRecipeList) { recipe ->
            val bundle = Bundle().apply {
                putLong("recipeId", recipe.recipeId)
                putString("name", recipe.name)
                putString("imageUrl", recipe.imageUrl)
                putBoolean("liked", recipe.liked)
            }
            Log.d("RecentRecipe", "최근 본 레시피 클릭: $recipe")
            findNavController().navigate(R.id.action_searchFragment_to_recipeFragment, bundle)
        }
        binding.searchRecentRecipeRv.adapter = recentRecipeAdapter
        binding.searchRecentRecipeRv.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        Log.d("RecentRecipe", "최근 본 레시피 RecyclerView 세팅 완료")
    }

    private fun observeRecentRecipes() {
        recentRecipeViewModel.recentRecipes.observe(viewLifecycleOwner) { recipes ->
            Log.d("RecentRecipe", "최근 본 레시피 옵저브 데이터 변경: ${recipes.size}개")
            recentRecipeList.clear()
            recentRecipeList.addAll(recipes)
            recentRecipeAdapter.notifyDataSetChanged()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}