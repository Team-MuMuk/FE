package com.example.mumuk.ui.recipe

import android.content.Context
import android.util.Log
import androidx.lifecycle.*
import com.example.mumuk.data.model.NutritionInfo
import com.example.mumuk.data.model.ShopItem
import com.example.mumuk.data.model.Recipe
import com.example.mumuk.data.model.RecipeIngredient
import com.example.mumuk.data.model.search.UserRecipeDetailData
import com.example.mumuk.data.repository.RecipeIngredientRepository
import com.example.mumuk.data.repository.ShopRepository
import com.example.mumuk.data.repository.UserRecipeRepository
import kotlinx.coroutines.launch

class RecipeViewModel(private val userRecipeRepository: UserRecipeRepository) : ViewModel() {

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

    private val _userRecipeDetail = MutableLiveData<UserRecipeDetailData>()
    val userRecipeDetail: LiveData<UserRecipeDetailData> = _userRecipeDetail

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

    fun fetchRecipeDetail(recipeId: Long) {
        Log.d("RecipeViewModel", "fetchRecipeDetail() called with recipeId: $recipeId")
        viewModelScope.launch {
            try {
                val response = userRecipeRepository.getUserRecipeDetail(recipeId)
                if (response.isSuccessful) {
                    Log.d("RecipeViewModel", "API success. Response body: ${response.body()}")
                    response.body()?.data?.let {
                        Log.d("RecipeViewModel", "Parsed detail data: $it")
                        _userRecipeDetail.value = it
                    }
                } else {
                    Log.e("RecipeViewModel", "API error: code=${response.code()}, message=${response.message()}")
                }
            } catch (e: Exception) {
                Log.e("RecipeViewModel", "Exception in fetchRecipeDetail: ${e.localizedMessage}", e)
            }
        }
    }

    class Factory(private val context: Context) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(RecipeViewModel::class.java)) {
                val repo = UserRecipeRepository(context)
                @Suppress("UNCHECKED_CAST")
                return RecipeViewModel(repo) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}