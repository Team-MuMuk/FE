package com.example.mumuk.ui.recipe

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mumuk.data.model.NutritionInfo
import com.example.mumuk.data.model.ShopItem
import com.example.mumuk.data.model.Recipe
import com.example.mumuk.data.model.RecipeIngredient
import com.example.mumuk.data.repository.RecipeIngredientRepository
import com.example.mumuk.data.repository.ShopRepository
import kotlinx.coroutines.launch

class RecipeViewModel : ViewModel() {

    private val shopRepository = ShopRepository()
    private val ingredientRepository = RecipeIngredientRepository()

    private val _nutritionInfoList = MutableLiveData<List<NutritionInfo>>()
    val nutritionInfoList: LiveData<List<NutritionInfo>> = _nutritionInfoList

    private val _shopItemList = MutableLiveData<List<ShopItem>>()
    val shopItemList: LiveData<List<ShopItem>> = _shopItemList

    private val _recipeList = MutableLiveData<List<Recipe>>()
    val recipeList: LiveData<List<Recipe>> = _recipeList

    private val _selectedRecipe = MutableLiveData<Recipe>()
    val selectedRecipe: LiveData<Recipe> = _selectedRecipe

    private val _allIngredients = MutableLiveData<List<RecipeIngredient>>()
    val allIngredients: LiveData<List<RecipeIngredient>> = _allIngredients

    init {
        loadShopItems()
        loadIngredients()
        _recipeList.value = listOf(
            Recipe(id = 1, img = null, title = "두부유부초밥", isLiked = false),
            Recipe(id = 2, img = null, title = "김치볶음밥", isLiked = false)
        )
        _selectedRecipe.value = _recipeList.value?.firstOrNull()
    }

    private fun loadShopItems() {
        viewModelScope.launch {
            _shopItemList.value = shopRepository.getShopItems()
        }
    }

    fun updateRecipeLike(recipeId: Long, isLiked: Boolean) {
        _recipeList.value = _recipeList.value?.map { recipe ->
            if (recipe.id == recipeId) recipe.copy(isLiked = isLiked) else recipe
        }
        if (_selectedRecipe.value?.id == recipeId) {
            _selectedRecipe.value = _selectedRecipe.value?.copy(isLiked = isLiked)
        }
    }

    private fun loadIngredients() {
        _allIngredients.value = ingredientRepository.getAllIngredients()
    }

    fun selectRecipe(recipe: Recipe) {
        _selectedRecipe.value = recipe
    }
}