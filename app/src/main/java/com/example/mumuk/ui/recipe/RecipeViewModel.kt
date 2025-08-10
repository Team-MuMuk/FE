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

    private val _shopItemList = MutableLiveData<List<ShopItem>>()
    val shopItemList: LiveData<List<ShopItem>> = _shopItemList

    private val _selectedRecipe = MutableLiveData<Recipe>()
    val selectedRecipe: LiveData<Recipe> = _selectedRecipe

    private val _allIngredients = MutableLiveData<List<RecipeIngredient>>()
    val allIngredients: LiveData<List<RecipeIngredient>> = _allIngredients

    private val _userRecipeDetail = MutableLiveData<UserRecipeDetailData>()
    val userRecipeDetail: LiveData<UserRecipeDetailData> = _userRecipeDetail

    init {
        loadShopItems()
        loadIngredients()
    }

    private fun loadShopItems() {
        viewModelScope.launch {
            _shopItemList.value = shopRepository.getShopItems()
        }
    }

    fun updateRecipeLike(recipeId: Long, isLiked: Boolean) {
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

    // --- 이 함수가 핵심입니다 ---
    fun fetchRecipeDetail(recipeId: Long) {
        Log.d("RecipeViewModel", "fetchRecipeDetail() called with recipeId: $recipeId")
        viewModelScope.launch {
            try {
                val response = userRecipeRepository.getUserRecipeDetail(recipeId)
                if (response.isSuccessful) {
                    Log.d("RecipeViewModel", "API success. Response body: ${response.body()}")
                    response.body()?.data?.let {
                        Log.d("RecipeViewModel", "Parsed detail data: $it")
                        _userRecipeDetail.postValue(it) // 백그라운드 스레드이므로 postValue 사용
                    }
                } else {
                    // --- 서버가 보낸 실제 에러 메시지를 확인하기 위한 로그 ---
                    val errorBody = response.errorBody()?.string() ?: "No error body"
                    Log.e("RecipeViewModel", "API error: code=${response.code()}, message=${response.message()}, errorBody=$errorBody")
                    // ----------------------------------------------------
                }
            } catch (e: Exception) {
                Log.e("RecipeViewModel", "Exception in fetchRecipeDetail: ${e.localizedMessage}", e)
            }
        }
    }
    // -------------------------

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