package com.example.mumuk.data.repository

import android.content.Context
import com.example.mumuk.data.api.RetrofitClient
import com.example.mumuk.data.model.Ingredient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class IngredientRepository(private val context: Context) {

    suspend fun getIngredients(): List<Ingredient> = withContext(Dispatchers.IO) {
        val response = RetrofitClient.getIngredientApi(context).getIngredients()
        if (response.isSuccessful) {
            response.body()?.data?.map { dto ->
                Ingredient(dto.name, dto.expireDate)
            } ?: emptyList()
        } else {
            emptyList()
        }
    }
}