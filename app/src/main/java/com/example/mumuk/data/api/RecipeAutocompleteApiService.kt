package com.example.mumuk.data.api

import com.example.mumuk.data.model.search.RecipeAutocompleteResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface RecipeAutocompleteApiService {
    @GET("/api/search/recipes/autocomplete")
    fun getRecipeAutocomplete(
        @Query("userInput") userInput: String
    ): Call<RecipeAutocompleteResponse>
}