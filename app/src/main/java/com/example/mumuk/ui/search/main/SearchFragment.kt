package com.example.mumuk.ui.search.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mumuk.R
import com.example.mumuk.databinding.FragmentSearchBinding
import com.example.mumuk.databinding.ItemSearchSuggestKeywordChipBinding
import com.example.mumuk.data.model.Recipe

class SearchFragment : Fragment() {
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private val recentKeywords = mutableListOf("포케", "샐러드", "스테이크", "파스타", "닭가슴살 도시락", "아보카도", "연어", "볶음밥")
    private val suggestKeywords = listOf("포케", "아보카도 샐러드", "샐러드", "닭가슴살", "건강주스", "키토김밥")
    private val popularKeywords = listOf("곤약밥 레시피", "곤약밥 레시피", "곤약밥 레시피", "곤약밥 레시피", "곤약밥 레시피", "곤약밥 레시피", "곤약밥 레시피", "곤약밥 레시피", "곤약밥 레시피", "곤약밥 레시피")

    private val recentRecipes = listOf(
        Recipe(
            imageResId = R.drawable.img_food_sample,
            title = "연어 포케",
            subtitle = "신선한 연어와 채소",
            isLiked = false
        ),
        Recipe(
            imageResId = R.drawable.img_food_sample,
            title = "바스크치즈케이크",
            subtitle = "진한 치즈 맛의 디저트",
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
        setupPopularKeywordList()
        setupRecentRecipeList()

        binding.searchEditEt.setOnClickListener {
            findNavController().navigate(R.id.action_searchFragment_to_searchAutocompleteFragment)
        }

        return binding.root
    }

    private fun setupRecentKeywordList() {
        val adapter = SearchRecentKeywordAdapter(recentKeywords) { keyword ->
            binding.searchEditEt.setText(keyword)
        }
        binding.searchRecentKeywordsRv.adapter = adapter
        binding.searchRecentKeywordsRv.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
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

    private fun setupPopularKeywordList() {
        val adapter = SearchPopularAdapter(popularKeywords)
        binding.searchPopularKeywordsRv.adapter = adapter
        binding.searchPopularKeywordsRv.layoutManager = GridLayoutManager(context, 2)
    }

    private fun setupRecentRecipeList() {
        val adapter = SearchRecentRecipeAdapter(recentRecipes)
        binding.searchRecentRecipeRv.adapter = adapter
        binding.searchRecentRecipeRv.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}