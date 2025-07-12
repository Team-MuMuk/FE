package com.example.mumuk.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mumuk.data.model.Recipe
import com.example.mumuk.data.repository.RecommendRecipeRepository // <--- 변경된 부분
import kotlinx.coroutines.launch

// ui/home 패키지에 위치합니다.
class RecommendViewModel : ViewModel() {

    // 변경된 이름의 Repository를 사용합니다.
    private val repository = RecommendRecipeRepository() // <--- 변경된 부분

    private val _recipes = MutableLiveData<List<Recipe>>()
    val recipes: LiveData<List<Recipe>> = _recipes

    init {
        loadRecipes()
    }

    private fun loadRecipes() {
        viewModelScope.launch {
            // repository의 메서드를 호출합니다.
            val recipeList = repository.getRecommendedRecipes()
            _recipes.value = recipeList
        }
    }
}