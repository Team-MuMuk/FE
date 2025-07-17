package com.example.mumuk.ui.search.result

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.mumuk.R
import com.example.mumuk.databinding.FragmentSearchResultBinding
import com.example.mumuk.data.model.Recipe

class SearchResultFragment : Fragment() {
    private var _binding: FragmentSearchResultBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: SearchResultAdapter

    private val dummyList = listOf(
        Recipe(
            img = R.drawable.img_food_sample,
            title = "연어 포케",
            isLiked = false
        ),
        Recipe(
            img = R.drawable.img_food_sample,
            title = "닭가슴살 포케",
            isLiked = false
        ),
        Recipe(
            img = R.drawable.img_food_sample,
            title = "아보카도 포케",
            isLiked = false
        ),
        Recipe(
            img = R.drawable.img_food_sample,
            title = "참치 포케",
            isLiked = false
        )
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchResultBinding.inflate(inflater, container, false)

        adapter = SearchResultAdapter(dummyList)
        binding.searchResultRv.layoutManager = GridLayoutManager(context, 2)
        binding.searchResultRv.adapter = adapter

        binding.searchResultEmpty.visibility =
            if (dummyList.isEmpty()) View.VISIBLE else View.GONE
        binding.searchResultRv.visibility =
            if (dummyList.isEmpty()) View.GONE else View.VISIBLE

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.searchResultBackBtn.setOnClickListener {
            findNavController().navigate(R.id.action_searchResultFragment_to_searchFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}