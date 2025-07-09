package com.example.mumuk.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.mumuk.R
import com.example.mumuk.databinding.FragmentSearchBinding
import com.example.mumuk.ui.category.CategoryRecipeCard

class SearchFragment : Fragment() {
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private val recentKeywords = listOf("포케", "샐러드", "스테이크", "파스타", "닭가슴살 도시락", "아보카도", "연어", "볶음밥")
    private val suggestKeywords = listOf("포케", "아보카도 샐러드", "샐러드", "닭가슴살", "건강주스", "키토김밥")
    private val popularKeywords = listOf("곤약밥 레시피", "곤약밥 레시피", "곤약밥 레시피", "곤약밥 레시피", "곤약밥 레시피", "곤약밥 레시피", "곤약밥 레시피", "곤약밥 레시피", "곤약밥 레시피", "곤약밥 레시피")

    private val recentRecipes = listOf(
        CategoryRecipeCard("헬스식단", "연어 포케"),
        CategoryRecipeCard("헬스식단", "바스크치즈케이크")
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)

        setupRecentKeywordChips(inflater)
        setupSuggestKeywordChips(inflater)
        setupPopularKeywordList()
        setupRecentRecipeList()

        binding.searchEditEt.setOnClickListener {
            findNavController().navigate(R.id.action_searchFragment_to_searchAutocompleteFragment)
        }

        return binding.root
    }

    private fun setupRecentKeywordChips(inflater: LayoutInflater) {
        val flexbox = binding.searchRecentKeywordsFl
        flexbox.removeAllViews()
        for (keyword in recentKeywords) {
            val chipBinding = com.example.mumuk.databinding.ItemSearchRecentKeywordChipBinding.inflate(inflater, flexbox, false)
            chipBinding.searchRecentKeywordTv.text = keyword
            chipBinding.searchRecentKeywordDeleteBtn.visibility = View.VISIBLE
            chipBinding.searchRecentKeywordDeleteBtn.setOnClickListener {
            }
            chipBinding.searchRecentKeywordTv.setOnClickListener {
                binding.searchEditEt.setText(keyword)
            }
            flexbox.addView(chipBinding.root)
        }
    }

    private fun setupSuggestKeywordChips(inflater: LayoutInflater) {
        val flexbox = binding.searchSuggestKeywordsFl
        flexbox.removeAllViews()
        for (keyword in suggestKeywords) {
            val chipBinding = com.example.mumuk.databinding.ItemSearchSuggestKeywordChipBinding.inflate(inflater, flexbox, false)
            chipBinding.searchSuggestKeywordTv.text = keyword
            chipBinding.searchSuggestKeywordTv.setOnClickListener {
                binding.searchEditEt.setText(keyword)
            }
            flexbox.addView(chipBinding.root)
        }
    }

    private fun setupPopularKeywordList() {
        val adapter = SearchPopularAdapter(popularKeywords)
        binding.searchPopularKeywordsRv.adapter = adapter
        binding.searchPopularKeywordsRv.layoutManager = GridLayoutManager(context, 2)
    }

    private fun setupRecentRecipeList() {
        val adapter = SearchRecentRecipeAdapter(recentRecipes)
        binding.searchRecentRecipeRv.adapter = adapter
        binding.searchRecentRecipeRv.layoutManager = GridLayoutManager(context, 2)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}