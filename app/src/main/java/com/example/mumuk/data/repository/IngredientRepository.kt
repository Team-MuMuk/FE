package com.example.mumuk.data.repository

import android.content.Context
import com.example.mumuk.data.api.RetrofitClient
import com.example.mumuk.data.model.Ingredient
import com.example.mumuk.data.model.ingredient.IngredientRegisterRequest
import com.example.mumuk.data.model.ingredient.IngredientRegisterResponse
import com.example.mumuk.data.model.ingredient.IngredientResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response

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

    suspend fun registerIngredient(
        name: String, expireDate: String, daySetting: String = "D7"
    ): Response<IngredientRegisterResponse> = withContext(Dispatchers.IO) {
        val request = IngredientRegisterRequest(name, expireDate, daySetting)
        RetrofitClient.getIngredientApi(context).registerIngredient(request)
    }
}