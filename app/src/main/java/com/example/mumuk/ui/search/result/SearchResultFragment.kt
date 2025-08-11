package com.example.mumuk.ui.search.result

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.core.os.bundleOf
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.mumuk.R
import com.example.mumuk.databinding.FragmentSearchResultBinding
import com.example.mumuk.data.api.RetrofitClient
import com.example.mumuk.data.model.Recipe
import com.example.mumuk.data.model.search.RecipeSearchResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchResultFragment : Fragment() {
    private var _binding: FragmentSearchResultBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: SearchResultAdapter

    private var currentKeyword: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchResultBinding.inflate(inflater, container, false)
        adapter = SearchResultAdapter(mutableListOf()) { recipe ->
            val bundle = bundleOf("recipeId" to recipe.id)
            findNavController().navigate(
                R.id.action_searchResultFragment_to_recipeFragment,
                bundle
            )
        }
        binding.searchResultRv.layoutManager = GridLayoutManager(context, 2)
        binding.searchResultRv.adapter = adapter

        binding.searchResultBackBtn.setOnClickListener {
            findNavController().navigate(R.id.action_searchResultFragment_to_searchFragment)
        }

        binding.searchResultEditEt.doOnTextChanged { text, _, _, _ ->
            currentKeyword = text?.toString() ?: ""
        }

        binding.searchResultBtn.setOnClickListener {
            Log.d("SearchResult", "검색 버튼 클릭됨: keyword = $currentKeyword")
            fetchSearchResult(currentKeyword)
        }

        binding.searchResultEditEt.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                Log.d("SearchResult", "키보드 검색 액션: keyword = $currentKeyword")
                fetchSearchResult(currentKeyword)
                true
            } else {
                false
            }
        }

        val initialKeyword = arguments?.getString("keyword") ?: ""
        if (initialKeyword.isNotEmpty()) {
            binding.searchResultEditEt.setText(initialKeyword)
            currentKeyword = initialKeyword
            Log.d("SearchResult", "초기 진입: keyword = $currentKeyword")
            fetchSearchResult(currentKeyword)
        }

        return binding.root
    }

    private fun fetchSearchResult(keyword: String) {
        if (keyword.isEmpty()) {
            Log.d("SearchResult", "검색어가 비어있음")
            showEmpty()
            return
        }

        Log.d("SearchResult", "검색 API 호출 시작: keyword = $keyword")
        val api = RetrofitClient.getRecipeSearchApi(requireContext())
        api.searchRecipes(keyword).enqueue(object : Callback<RecipeSearchResponse> {
            override fun onResponse(
                call: Call<RecipeSearchResponse>,
                response: Response<RecipeSearchResponse>
            ) {
                Log.d("SearchResult", "API onResponse 호출됨")
                Log.d("SearchResult", "response.code = ${response.code()}")
                Log.d("SearchResult", "response.raw = ${response.raw()}")
                Log.d("SearchResult", "response.body = ${response.body()}")
                Log.d("SearchResult", "response.errorBody = ${response.errorBody()?.string()}")

                val result = response.body()
                Log.d("SearchResult", "result = $result")

                if (result?.data == null) {
                    Log.d("SearchResult", "result.data is null")
                } else {
                    Log.d("SearchResult", "result.data size = ${result.data.size}")
                }

                val recipes = result?.data?.map {
                    Recipe(
                        id = it.recipeId,
                        img = null,
                        title = it.name ?: "알 수 없음",
                        isLiked = it.liked,
                        recipeImageUrl = it.imageUrl
                    )
                } ?: emptyList()

                Log.d("SearchResult", "파싱된 레시피 개수 = ${recipes.size}")
                for (r in recipes) {
                    Log.d("SearchResult", "Recipe: id=${r.id}, title=${r.title}, img=${r.img}, isLiked=${r.isLiked}, recipeImageUrl=${r.recipeImageUrl}")
                }
                adapter.updateList(recipes)
                Log.d("SearchResult", "adapter.updateList 호출됨")

                if (recipes.isEmpty()) {
                    Log.d("SearchResult", "검색 결과 없음")
                    showEmpty()
                } else {
                    Log.d("SearchResult", "검색 결과 있음")
                    showResult()
                }
            }

            override fun onFailure(call: Call<RecipeSearchResponse>, t: Throwable) {
                Log.e("SearchResult", "API onFailure 호출됨: ${t.message}", t)
                showEmpty()
            }
        })
    }

    private fun showEmpty() {
        binding.searchResultEmpty.visibility = View.VISIBLE
        binding.searchResultRv.visibility = View.GONE
    }

    private fun showResult() {
        binding.searchResultEmpty.visibility = View.GONE
        binding.searchResultRv.visibility = View.VISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}