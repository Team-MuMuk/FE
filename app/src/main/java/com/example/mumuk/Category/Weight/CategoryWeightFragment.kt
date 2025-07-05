package com.example.mumuk.Category.Weight

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

class CategoryWeightFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_category_weight, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view)
        recyclerView.layoutManager = GridLayoutManager(context, 2)
        recyclerView.adapter = CategoryItemAdapter(getDummyList()) { item ->
            when (item.name) {
                "체중 감량" -> {
                    parentFragmentManager.beginTransaction()
                        .replace(R.id.category_content_container_fl, CategoryWeightLossFragment())
                        .addToBackStack(null)
                        .commit()
                }
                "근육 증가" -> {
                    parentFragmentManager.beginTransaction()
                        .replace(R.id.category_content_container_fl, CategoryMuscleGainFragment())
                        .addToBackStack(null)
                        .commit()
                }
            }
        }
    }

    private fun getDummyList(): List<CategoryItem> {
        return listOf(
            CategoryItem("체중 감량", R.drawable.ic_category_weight_1),
            CategoryItem("근육 증가", R.drawable.ic_category_weight_2)
        )
    }
}