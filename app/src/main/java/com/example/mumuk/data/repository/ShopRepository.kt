package com.example.mumuk.data.repository

import android.content.Context
import com.example.mumuk.data.api.RetrofitClient
import com.example.mumuk.data.model.recipe.NaverShoppingItem

class ShopRepository(private val context: Context) {
    suspend fun getNaverShoppingItems(recipeId: Long): List<NaverShoppingItem> {
        val response = RetrofitClient.getNaverShoppingApi(context).getNaverShoppings(recipeId)
        return response.naverShoppings
    }
}