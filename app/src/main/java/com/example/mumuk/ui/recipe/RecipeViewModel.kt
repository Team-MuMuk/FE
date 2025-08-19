package com.example.mumuk.ui.recipe

import android.content.Context
import android.util.Log
import androidx.lifecycle.*
import com.example.mumuk.data.api.RetrofitClient
import com.example.mumuk.data.model.Recipe
import com.example.mumuk.data.model.RecipeIngredient
import com.example.mumuk.data.model.recipe.IngredientMatchData
import com.example.mumuk.data.model.recipe.NaverShoppingItem
import com.example.mumuk.data.model.recipe.SearchedBlog
import com.example.mumuk.data.model.search.UserRecipeDetailData
import com.example.mumuk.data.repository.OgImageRepository
import com.example.mumuk.data.repository.RecipeIngredientRepository
import com.example.mumuk.data.repository.ShopRepository
import com.example.mumuk.data.repository.UserRecipeRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch

class RecipeViewModel(private val userRecipeRepository: UserRecipeRepository, private val context: Context) : ViewModel() {

    private val shopRepository = ShopRepository(context)
    private val ingredientRepository = RecipeIngredientRepository()
    private val ogImageRepository = OgImageRepository

    private val _shopItemList = MutableLiveData<List<NaverShoppingItem>>()
    val shopItemList: LiveData<List<NaverShoppingItem>> = _shopItemList

    private val _selectedRecipe = MutableLiveData<Recipe>()
    val selectedRecipe: LiveData<Recipe> = _selectedRecipe

    private val _allIngredients = MutableLiveData<List<RecipeIngredient>>()
    val allIngredients: LiveData<List<RecipeIngredient>> = _allIngredients

    private val _userRecipeDetail = MutableLiveData<UserRecipeDetailData>()
    val userRecipeDetail: LiveData<UserRecipeDetailData> = _userRecipeDetail

    private val _blogList = MutableLiveData<List<SearchedBlog>>()
    val blogList: LiveData<List<SearchedBlog>> = _blogList

    private val _ingredientMatchData = MutableLiveData<IngredientMatchData>()
    val ingredientMatchData: LiveData<IngredientMatchData> = _ingredientMatchData

    init {
        loadIngredients()
    }

    fun fetchShopItems(recipeId: Long) {
        viewModelScope.launch {
            try {
                val items = shopRepository.getNaverShoppingItems(recipeId)
                _shopItemList.value = items
            } catch (e: Exception) {
                Log.e("RecipeViewModel", "Exception in fetchShopItems: ${e.localizedMessage}", e)
                _shopItemList.value = emptyList()
            }
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

    fun fetchRecipeDetail(recipeId: Long) {
        Log.d("RecipeViewModel", "fetchRecipeDetail() called with recipeId: $recipeId")
        viewModelScope.launch {
            try {
                val response = userRecipeRepository.getUserRecipeDetail(recipeId)
                if (response.isSuccessful) {
                    Log.d("RecipeViewModel", "API success. Response body: ${response.body()}")
                    response.body()?.data?.let {
                        Log.d("RecipeViewModel", "Parsed detail data: $it")
                        _userRecipeDetail.postValue(it)
                        fetchBlogs(it.title)
                        fetchShopItems(recipeId) // 네이버 쇼핑 불러오기 추가!
                    }
                } else {
                    val errorBody = response.errorBody()?.string() ?: "No error body"
                    Log.e("RecipeViewModel", "API error: code=${response.code()}, message=${response.message()}, errorBody=$errorBody")
                }
            } catch (e: Exception) {
                Log.e("RecipeViewModel", "Exception in fetchRecipeDetail: ${e.localizedMessage}", e)
            }
        }
    }

    fun fetchIngredientMatch(recipeId: Long) {
        viewModelScope.launch {
            try {
                val api = RetrofitClient.getRecipeApiService(context)
                val response = api.getIngredientMatch(recipeId)
                _ingredientMatchData.value = response.data
            } catch (e: Exception) {
                Log.e("RecipeViewModel", "Exception in fetchIngredientMatch: ${e.localizedMessage}", e)
            }
        }
    }

    private fun fetchBlogs(keyword: String) {
        Log.d("RecipeViewModel", "fetchBlogs() called with keyword: $keyword")
        viewModelScope.launch {
            try {
                val response = userRecipeRepository.searchBlogs(keyword)
                if (response.isSuccessful) {
                    val blogs = response.body()?.blogs ?: emptyList()
                    Log.d("RecipeViewModel", "Blog search API success. Found ${blogs.size} blogs.")

                    val updatedBlogs = blogs.map { blog ->
                        async {
                            val ogImage = ogImageRepository.fetchOgImage(blog.link)
                            blog.ogImageUrl = ogImage
                            blog
                        }
                    }.awaitAll()

                    _blogList.postValue(updatedBlogs)
                } else {
                    val errorBody = response.errorBody()?.string() ?: "No error body"
                    Log.e("RecipeViewModel", "Blog search API error: code=${response.code()}, message=${response.message()}, errorBody=$errorBody")
                }
            } catch (e: Exception) {
                Log.e("RecipeViewModel", "Exception in fetchBlogs: ${e.localizedMessage}", e)
            }
        }
    }

    class Factory(private val context: Context) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(RecipeViewModel::class.java)) {
                val repo = UserRecipeRepository(context)
                @Suppress("UNCHECKED_CAST")
                return RecipeViewModel(repo, context) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}