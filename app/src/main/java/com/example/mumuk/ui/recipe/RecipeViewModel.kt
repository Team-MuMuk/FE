package com.example.mumuk.ui.recipe

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mumuk.data.model.NutritionInfo
import com.example.mumuk.data.model.ShopItem
import com.example.mumuk.data.model.Recipe
import com.example.mumuk.data.repository.NutritionInfoRepository
import com.example.mumuk.data.repository.ShopRepository
import kotlinx.coroutines.launch

class RecipeViewModel : ViewModel() {

    private val nutritionInfoRepository = NutritionInfoRepository()
    private val shopRepository = ShopRepository()

    private val _nutritionInfoList = MutableLiveData<List<NutritionInfo>>()
    val nutritionInfoList: LiveData<List<NutritionInfo>> = _nutritionInfoList

    private val _shopItemList = MutableLiveData<List<ShopItem>>()
    val shopItemList: LiveData<List<ShopItem>> = _shopItemList

    private val _recipeList = MutableLiveData<List<Recipe>>()
    val recipeList: LiveData<List<Recipe>> = _recipeList

    private val _selectedRecipe = MutableLiveData<Recipe>()
    val selectedRecipe: LiveData<Recipe> = _selectedRecipe

    init {
        loadNutritionInfo()
        loadShopItems()
        _recipeList.value = listOf(
            Recipe(id = 1, img = null, title = "두부유부초밥", isLiked = false),
            Recipe(id = 2, img = null, title = "김치볶음밥", isLiked = false)
        )
        _selectedRecipe.value = _recipeList.value?.firstOrNull()
    }

    private fun loadNutritionInfo() {
        viewModelScope.launch {
            _nutritionInfoList.value = nutritionInfoRepository.getNutritionInfoList()
        }
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

    fun selectRecipe(recipe: Recipe) {
        _selectedRecipe.value = recipe
    }
}