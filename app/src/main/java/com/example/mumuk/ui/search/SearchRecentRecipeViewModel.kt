package com.example.mumuk.ui.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mumuk.data.api.RecentRecipeApiService
import com.example.mumuk.data.model.search.RecentRecipe
import com.example.mumuk.data.model.search.RecentRecipeResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchRecentRecipeViewModel(private val apiService: RecentRecipeApiService) : ViewModel() {
    private val _recentRecipes = MutableLiveData<List<RecentRecipe>>()
    val recentRecipes: LiveData<List<RecentRecipe>> = _recentRecipes

    fun fetchRecentRecipes() {
        apiService.getRecentRecipes().enqueue(object : Callback<RecentRecipeResponse> {
            override fun onResponse(
                call: Call<RecentRecipeResponse>,
                response: Response<RecentRecipeResponse>
            ) {
                if (response.isSuccessful) {
                    val recipes = response.body()?.data?.recipeSummaries ?: emptyList()
                    _recentRecipes.postValue(recipes)
                } else {
                    _recentRecipes.postValue(emptyList())
                }
            }
            override fun onFailure(call: Call<RecentRecipeResponse>, t: Throwable) {
                _recentRecipes.postValue(emptyList())
            }
        })
    }
}