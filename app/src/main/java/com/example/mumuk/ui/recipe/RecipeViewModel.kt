package com.example.mumuk.ui.recipe

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mumuk.data.model.NutritionInfo
import com.example.mumuk.data.model.ShopItem
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

    init {
        loadNutritionInfo()
        loadShopItems()
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
}