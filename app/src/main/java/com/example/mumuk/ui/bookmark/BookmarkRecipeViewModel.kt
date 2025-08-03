package com.example.mumuk.ui.bookmark

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mumuk.data.model.Recipe
import com.example.mumuk.data.repository.BookmarkRecipeRepository
import kotlinx.coroutines.launch

enum class RecipeCategory {
    WEIGHT, HEALTH, RANDOM
}

class BookmarkRecipeViewModel : ViewModel() {

    private val repository = BookmarkRecipeRepository()
    private val _recipes = MutableLiveData<List<Recipe>>()
    val recipes: LiveData<List<Recipe>> = _recipes

    init {
        loadRecipes(RecipeCategory.WEIGHT)
    }

    fun loadRecipes(category: RecipeCategory) {
        viewModelScope.launch {
            val recipeList = when (category) {
                RecipeCategory.WEIGHT -> repository.getWeightRecipes()
                RecipeCategory.HEALTH -> repository.getHealthRecipes()
                RecipeCategory.RANDOM -> repository.getRandomRecipes()
            }
            _recipes.value = recipeList
        }
    }
}