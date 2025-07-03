package com.example.mumuk

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class CategoryHealthFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_category_health, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view)
        recyclerView.layoutManager = GridLayoutManager(context, 2)
        recyclerView.adapter = CategoryItemAdapter(getDummyList())
    }

    private fun getDummyList(): List<CategoryItem> {
        return listOf(
            CategoryItem("당 줄이기", R.drawable.ic_category_health_1),
            CategoryItem("혈압관리", R.drawable.ic_category_health_2),
            CategoryItem("콜레스테롤 관리", R.drawable.ic_category_health_3),
            CategoryItem("소화 건강", R.drawable.ic_category_health_4)
        )
    }
}