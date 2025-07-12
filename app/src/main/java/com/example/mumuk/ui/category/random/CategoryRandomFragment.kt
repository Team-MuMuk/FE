package com.example.mumuk.ui.category.Random

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mumuk.ui.category.CategoryFragment
import com.example.mumuk.ui.category.CategoryItem
import com.example.mumuk.ui.category.CategoryItemAdapter
import com.example.mumuk.R

class CategoryRandomFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_category_random, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view)
        recyclerView.layoutManager = GridLayoutManager(context, 2)

        recyclerView.adapter = CategoryItemAdapter(getDummyList()) { item ->
            val selectedTab = item.name
            (parentFragment as? CategoryFragment)?.showFullScreenFragment(
                CategoryRandomRecipeFragment.newInstance(selectedTab)
            )
        }
    }

    private fun getDummyList(): List<CategoryItem> {
        return listOf(
            CategoryItem("랜덤식단", R.drawable.ic_category_random_1)
        )
    }
}
