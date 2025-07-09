package com.example.mumuk.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mumuk.R
import com.example.mumuk.databinding.FragmentSearchAutocompleteBinding
import com.example.mumuk.ui.MainActivity

class SearchAutocompleteFragment : Fragment() {
    private var _binding: FragmentSearchAutocompleteBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: SearchAutocompleteAdapter

    private var keywordList = listOf(
        SearchAutocompleteKeyword("포케", true),
        SearchAutocompleteKeyword("연어포케", false),
        SearchAutocompleteKeyword("훈제오리포케", false)
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchAutocompleteBinding.inflate(inflater, container, false)

        adapter = SearchAutocompleteAdapter(keywordList)
        binding.searchAutocompleteRv.adapter = adapter
        binding.searchAutocompleteRv.layoutManager = LinearLayoutManager(context)

        binding.searchAutocompleteEditEt.setText("")
        binding.searchAutocompleteEditEt.requestFocus()

        binding.searchAutocompleteEditEt.setOnEditorActionListener { v, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                findNavController().navigate(R.id.action_searchAutocompleteFragment_to_searchResultFragment)
                true
            } else {
                false
            }
        }

        binding.searchAutocompleteEditEt.addTextChangedListener {
            val query = it?.toString() ?: ""
        }

        return binding.root
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