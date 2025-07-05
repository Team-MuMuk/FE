package com.example.mumuk.Category.Weight

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mumuk.Category.CategoryRecipeCard
import com.example.mumuk.Category.CategoryRecipeCardAdapter
import com.example.mumuk.R

class CategoryWeightLossFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_category_recipe_list, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val recycler = view.findViewById<RecyclerView>(R.id.category_recipe_recycler_view)
        recycler.layoutManager = GridLayoutManager(context, 2)
        recycler.adapter = CategoryRecipeCardAdapter(listOf(
            CategoryRecipeCard("헬스식단", "연어 포케"),
            CategoryRecipeCard("헬스식단", "닭가슴살 샐러드")
        ))
    }
}