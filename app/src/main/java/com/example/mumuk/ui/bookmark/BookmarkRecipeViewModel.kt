package com.example.mumuk.ui.bookmark

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mumuk.data.model.Recipe
import com.example.mumuk.data.repository.BookmarkRecipeRepository
import kotlinx.coroutines.launch

class BookmarkRecipeViewModel : ViewModel() {

    private val repository = BookmarkRecipeRepository()

    private val _recipes = MutableLiveData<List<Recipe>>()
    val recipes: LiveData<List<Recipe>> = _recipes

    init {
        loadBookmarkedRecipes()
    }

    private fun loadBookmarkedRecipes() {
        viewModelScope.launch {
            val recipeList = repository.getBookmarkedRecipes()
            _recipes.value = recipeList
        }
    }
}