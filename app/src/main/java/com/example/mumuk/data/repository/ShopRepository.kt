package com.example.mumuk.data.repository

import com.example.mumuk.R
import com.example.mumuk.data.model.ShopItem

class ShopRepository {

    suspend fun getShopItems(): List<ShopItem> {
        return listOf(
            ShopItem(R.drawable.img_shop_sample, "토마토", "9,900원"),
            ShopItem(R.drawable.img_shop_sample, "토마토마토", "12,000원"),
            ShopItem(R.drawable.img_shop_sample, "토마토마", "2,500원"),
            ShopItem(R.drawable.img_shop_sample, "마토마", "3,000원")
        )
    }
}