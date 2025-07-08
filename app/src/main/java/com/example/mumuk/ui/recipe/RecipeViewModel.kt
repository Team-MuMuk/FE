package com.example.mumuk.ui.recipe

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mumuk.data.model.NutritionInfo
import com.example.mumuk.data.repository.RecipeDetailRepository
import kotlinx.coroutines.launch

// 이 파일은 ui/recipe 패키지에 생성합니다.
class RecipeViewModel : ViewModel() {

    private val repository = RecipeDetailRepository()

    // 영양 정보 데이터를 담을 LiveData
    private val _nutritionInfoList = MutableLiveData<List<NutritionInfo>>()
    val nutritionInfoList: LiveData<List<NutritionInfo>> = _nutritionInfoList

    init {
        loadNutritionInfo()
    }

    private fun loadNutritionInfo() {
        viewModelScope.launch {
            _nutritionInfoList.value = repository.getNutritionInfoList()
        }
    }
}