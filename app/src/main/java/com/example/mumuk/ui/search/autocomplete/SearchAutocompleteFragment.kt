package com.example.mumuk.ui.search.autocomplete

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mumuk.R
import com.example.mumuk.data.api.RetrofitClient
import com.example.mumuk.data.model.search.RecipeAutocompleteResponse
import com.example.mumuk.data.model.search.RecentSearchResponse
import com.example.mumuk.databinding.FragmentSearchAutocompleteBinding
import com.example.mumuk.ui.MainActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchAutocompleteFragment : Fragment() {
    private var _binding: FragmentSearchAutocompleteBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: SearchAutocompleteAdapter
    private var keywordList = mutableListOf<SearchAutocompleteKeyword>()

    private var searchInProgress = false
    private var lastSavedKeyword: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("LifeDebug", "SearchAutocompleteFragment onCreate")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d("LifeDebug", "SearchAutocompleteFragment onCreateView")
        _binding = FragmentSearchAutocompleteBinding.inflate(inflater, container, false)

        adapter = SearchAutocompleteAdapter(keywordList) { keyword ->
            binding.searchAutocompleteEditEt.setText(keyword)
            handleSearchAndNavigate()
        }
        binding.searchAutocompleteRv.adapter = adapter
        binding.searchAutocompleteRv.layoutManager = LinearLayoutManager(context)

        binding.searchAutocompleteEditEt.setText("")
        binding.noRecipeTv.visibility = View.GONE

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("LifeDebug", "SearchAutocompleteFragment onViewCreated")

        binding.searchAutocompleteEditEt.requestFocus()
        view.postDelayed({
            val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(binding.searchAutocompleteEditEt, InputMethodManager.SHOW_IMPLICIT)
        }, 100)

        binding.searchAutocompleteEditEt.setOnEditorActionListener { _, actionId, _ ->
            Log.d("LifeDebug", "setOnEditorActionListener triggered")
            if (actionId == EditorInfo.IME_ACTION_SEARCH
                || actionId == EditorInfo.IME_ACTION_DONE
                || actionId == EditorInfo.IME_NULL
            ) {
                handleSearchAndNavigate()
                true
            } else {
                false
            }
        }
        binding.searchAutocompleteBtn.setOnClickListener {
            Log.d("LifeDebug", "searchAutocompleteBtn clicked")
            handleSearchAndNavigate()
        }

        binding.searchAutocompleteEditEt.addTextChangedListener {
            val query = it?.toString() ?: ""
            fetchAutocompleteKeywords(query)
        }
    }

    override fun onResume() {
        super.onResume()
        Log.d("LifeDebug", "SearchAutocompleteFragment onResume")
        (activity as? MainActivity)?.hideBottomNav()
    }

    override fun onPause() {
        super.onPause()
        Log.d("LifeDebug", "SearchAutocompleteFragment onPause")
        (activity as? MainActivity)?.showBottomNav()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d("LifeDebug", "SearchAutocompleteFragment onDestroyView")
        binding.searchAutocompleteEditEt.setOnEditorActionListener(null)
        binding.searchAutocompleteBtn.setOnClickListener(null)
        _binding = null
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("LifeDebug", "SearchAutocompleteFragment onDestroy")
    }

    private fun fetchAutocompleteKeywords(query: String) {
        if (query.isBlank()) {
            keywordList.clear()
            adapter.notifyDataSetChanged()
            binding.noRecipeTv.visibility = View.GONE
            Log.d("AutoDebug", "Query is blank. Clearing list/adapter.")
            return
        }
        val context = context ?: return
        Log.d("AutoDebug", "Calling API for query: $query")
        val api = RetrofitClient.getRecipeAutocompleteApi(context)
        api.getRecipeAutocomplete(query).enqueue(object : Callback<RecipeAutocompleteResponse> {
            override fun onResponse(
                call: Call<RecipeAutocompleteResponse>,
                response: Response<RecipeAutocompleteResponse>
            ) {
                Log.d("AutoDebug", "onResponse: isSuccessful=${response.isSuccessful}, code=${response.code()}, body=${response.body()}")
                val body = response.body()
                val keywords = body?.data ?: emptyList()
                Log.d("AutoDebug", "Parsed keywords: $keywords")
                keywordList.clear()
                keywords.forEachIndexed { idx, keyword ->
                    keywordList.add(SearchAutocompleteKeyword(keyword, idx == 0))
                }
                adapter.notifyDataSetChanged()
                binding.noRecipeTv.visibility = if (keywordList.isEmpty()) View.VISIBLE else View.GONE
            }
            override fun onFailure(call: Call<RecipeAutocompleteResponse>, t: Throwable) {
                Log.e("AutoDebug", "onFailure: ${t.message}", t)
                keywordList.clear()
                adapter.notifyDataSetChanged()
                binding.noRecipeTv.visibility = View.VISIBLE
            }
        })
    }

    private fun handleSearchAndNavigate() {
        Log.d("LifeDebug", "handleSearchAndNavigate called")
        if (searchInProgress) {
            Log.d("LifeDebug", "searchInProgress true. Return.")
            return
        }
        searchInProgress = true

        val keyword = binding.searchAutocompleteEditEt.text.toString().trim()
        if (keyword.isEmpty() || lastSavedKeyword == keyword) {
            Log.d("LifeDebug", "keyword empty or already saved. Return.")
            searchInProgress = false
            return
        }
        lastSavedKeyword = keyword
        Log.d("SearchDebug", "handleSearchAndNavigate 호출됨: $keyword")
        saveRecentKeyword(keyword)

        val bundle = Bundle().apply { putString("keyword", keyword) }
        val navController = findNavController()
        if (navController.currentDestination?.id == R.id.searchAutocompleteFragment) {
            navController.navigate(R.id.action_searchAutocompleteFragment_to_searchResultFragment, bundle)
        }
        binding.root.postDelayed({ searchInProgress = false }, 500)
    }

    private fun saveRecentKeyword(keyword: String) {
        Log.d("SearchDebug", "saveRecentKeyword 호출됨: $keyword")
        val context = context ?: return
        val api = RetrofitClient.getRecentSearchApi(context)
        api.saveRecentSearch(keyword).enqueue(object : Callback<RecentSearchResponse> {
            override fun onResponse(call: Call<RecentSearchResponse>, response: Response<RecentSearchResponse>) {}
            override fun onFailure(call: Call<RecentSearchResponse>, t: Throwable) {}
        })
    }
}