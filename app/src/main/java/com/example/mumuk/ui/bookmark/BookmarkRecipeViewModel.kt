package com.example.mumuk.ui.bookmark

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewModelScope
import com.example.mumuk.data.api.RetrofitClient
import com.example.mumuk.data.model.Recipe
import com.example.mumuk.data.repository.BookmarkRecipeRepository
import com.example.mumuk.data.repository.RecipeCategory
import kotlinx.coroutines.launch
import android.content.Context

class BookmarkRecipeViewModel(
    private val repo: BookmarkRecipeRepository
) : ViewModel() {

    private val _recipes = MutableLiveData<List<Recipe>>(emptyList())
    val recipes: LiveData<List<Recipe>> = _recipes

    fun loadRecipes(category: RecipeCategory) {
        viewModelScope.launch {
            try {
                val recipeList = when (category) {
                    RecipeCategory.WEIGHT -> repo.getWeightRecipes()
                    RecipeCategory.HEALTH -> repo.getHealthRecipes()
                    RecipeCategory.ALL    -> repo.getAllBookmarkedRecipes()
                }
                _recipes.value = recipeList
            } catch (e: Exception) {
                android.util.Log.e("BOOKMARK_VM", "loadRecipes failed", e)
                _recipes.value = emptyList()
            }
        }
    }


    fun onHeartClick(recipe: Recipe) {
        val current = _recipes.value?.toMutableList() ?: return
        val idx = current.indexOfFirst { it.id == recipe.id }
        if (idx == -1) return

        val removed = current.removeAt(idx)
        _recipes.value = current.toList()

        viewModelScope.launch {
            try {
                repo.toggleLike(removed.id)
            } catch (e: Exception) {
                val rollback = _recipes.value?.toMutableList() ?: mutableListOf()
                val insertIdx = idx.coerceAtMost(rollback.size)
                rollback.add(insertIdx, removed)
                _recipes.postValue(rollback.toList())
            }
        }
    }


    companion object {
        fun provide(context: Context): BookmarkRecipeViewModel {
            val repo = BookmarkRecipeRepository(
                categoryApi = RetrofitClient.getCategoryRecipeApi(context),
                userRecipeApiService = RetrofitClient.getUserRecipeApi(context)
            )
            return BookmarkRecipeViewModel(repo)
        }
    }
}
