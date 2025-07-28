package com.example.mumuk.ui.search.autocomplete

import android.content.Context
import android.os.Bundle
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchAutocompleteBinding.inflate(inflater, container, false)

        adapter = SearchAutocompleteAdapter(keywordList)
        binding.searchAutocompleteRv.adapter = adapter
        binding.searchAutocompleteRv.layoutManager = LinearLayoutManager(context)

        binding.searchAutocompleteEditEt.setText("")

        binding.noRecipeTv.visibility = View.GONE

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.searchAutocompleteEditEt.requestFocus()
        view.postDelayed({
            val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(binding.searchAutocompleteEditEt, InputMethodManager.SHOW_IMPLICIT)
        }, 100)

        binding.searchAutocompleteEditEt.setOnEditorActionListener { _, actionId, _ ->
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
            handleSearchAndNavigate()
        }

        binding.searchAutocompleteEditEt.addTextChangedListener {
            val query = it?.toString() ?: ""
            fetchAutocompleteKeywords(query)
        }
    }

    private fun fetchAutocompleteKeywords(query: String) {
        if (query.isBlank()) {
            keywordList.clear()
            adapter.notifyDataSetChanged()
            binding.noRecipeTv.visibility = View.GONE
            return
        }
        val context = context ?: return
        val api = RetrofitClient.getRecipeAutocompleteApi(context)
        api.getRecipeAutocomplete(query).enqueue(object : Callback<RecipeAutocompleteResponse> {
            override fun onResponse(
                call: Call<RecipeAutocompleteResponse>,
                response: Response<RecipeAutocompleteResponse>
            ) {
                val body = response.body()
                val keywords = body?.data ?: emptyList()
                keywordList.clear()
                keywords.forEachIndexed { idx, keyword ->
                    keywordList.add(
                        SearchAutocompleteKeyword(keyword, idx == 0)
                    )
                }
                adapter.notifyDataSetChanged()

                if (keywordList.isEmpty()) {
                    binding.noRecipeTv.visibility = View.VISIBLE
                } else {
                    binding.noRecipeTv.visibility = View.GONE
                }
            }
            override fun onFailure(call: Call<RecipeAutocompleteResponse>, t: Throwable) {
                keywordList.clear()
                adapter.notifyDataSetChanged()
                binding.noRecipeTv.visibility = View.VISIBLE
            }
        })
    }

    private fun handleSearchAndNavigate() {
        val keyword = binding.searchAutocompleteEditEt.text.toString().trim()
        if (keyword.isEmpty()) return
        saveRecentKeyword(keyword)

        val bundle = Bundle().apply {
            putString("keyword", keyword)
        }
        val navController = findNavController()
        if (navController.currentDestination?.id == R.id.searchAutocompleteFragment) {
            navController.navigate(R.id.action_searchAutocompleteFragment_to_searchResultFragment, bundle)
        }
    }

    private fun saveRecentKeyword(keyword: String) {
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

    override fun onResume() {
        super.onResume()
        (activity as? MainActivity)?.hideBottomNav()
    }

    override fun onPause() {
        super.onPause()
        (activity as? MainActivity)?.showBottomNav()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}