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
                navigateToResult()
                true
            } else {
                false
            }
        }

        binding.searchAutocompleteBtn.setOnClickListener {
            navigateToResult()
        }


        binding.searchAutocompleteBtn.setOnClickListener {
            navigateToResult()
        }

        binding.searchAutocompleteEditEt.addTextChangedListener {
            val query = it?.toString() ?: ""
        }
    }

    private fun navigateToResult() {
        val navController = findNavController()
        if (navController.currentDestination?.id == R.id.searchAutocompleteFragment) {
            navController.navigate(R.id.action_searchAutocompleteFragment_to_searchResultFragment)
        }
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