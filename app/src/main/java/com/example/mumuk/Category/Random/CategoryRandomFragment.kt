package com.example.mumuk.Category.Random

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mumuk.Category.CategoryItem
import com.example.mumuk.Category.CategoryItemAdapter
import com.example.mumuk.R

class CategoryRandomFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_category_random, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view)
        recyclerView.layoutManager = GridLayoutManager(context, 2)
        recyclerView.adapter = CategoryItemAdapter(getDummyList()) { item ->
            if (item.name == "랜덤 식단") {
                parentFragmentManager.beginTransaction()
                    .replace(R.id.category_content_container_fl, CategoryRandomRecipeFragment())
                    .addToBackStack(null)
                    .commit()
            }
        }
    }

    private fun getDummyList(): List<CategoryItem> {
        return listOf(
            CategoryItem("랜덤 식단", R.drawable.ic_category_random_1)
        )
    }
}