package com.example.mumuk.data.api

import android.content.Context
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    private const val BASE_URL = "https://api.mumuk.site"
    private var retrofit: Retrofit? = null

    private fun getRetrofit(context: Context): Retrofit {
        if (retrofit == null) {
            val client = OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS) 
                .addInterceptor(AuthInterceptor(context))
                .build()

            retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()
        }
        return retrofit!!
    }

    fun getAuthApi(context: Context): AuthApiService {
        return getRetrofit(context).create(AuthApiService::class.java)
    }

    //TODO: 다른 api도 getAuthApi처럼 get~~Api 와 같이 추가해서 쓰기

    fun getUserApi(context: Context): UserApiService {
        return getRetrofit(context).create(UserApiService::class.java)
    }

    fun getRecentSearchApi(context: Context): RecentSearchApiService {
        return getRetrofit(context).create(RecentSearchApiService::class.java)
    }

    fun getPopularKeywordApi(context: Context): PopularKeywordApiService {
        return getRetrofit(context).create(PopularKeywordApiService::class.java)
    }

    fun getRecipeAutocompleteApi(context: Context): RecipeAutocompleteApiService {
        return getRetrofit(context).create(RecipeAutocompleteApiService::class.java)
    }

    fun getSuggestKeywordApi(context: Context): SuggestKeywordApiService {
        return getRetrofit(context).create(SuggestKeywordApiService::class.java)
    }

    fun getAllergyApi(context: Context): AllergyApiService {
        return getRetrofit(context).create(AllergyApiService::class.java)
    }

    fun getRecipeApi(context: Context): RecentRecipeApiService {
        return getRetrofit(context).create(RecentRecipeApiService::class.java)
    }

    fun getIngredientApi(context: Context): IngredientApiService {
        return getRetrofit(context).create(IngredientApiService::class.java)
    }

    fun getUserRecipeApi(context: Context): UserRecipeApiService {
        return getRetrofit(context).create(UserRecipeApiService::class.java)
    }

    fun getRecipeSearchApi(context: Context): RecipeSearchApiService {
        return getRetrofit(context).create(RecipeSearchApiService::class.java)
    }

    fun getRandomRecipeApi(context: Context): RandomRecipeApiService {
        return getRetrofit(context).create(RandomRecipeApiService::class.java)
    }

    fun getCategoryRecipeApi(context: Context): CategoryRecipeApiService {
        return getRetrofit(context).create(CategoryRecipeApiService::class.java)
    }

    fun getRecentRecipeApi(context: Context): RecentRecipeApiService =
        getRetrofit(context).create(RecentRecipeApiService::class.java)

    fun getRecipeApiService(context: Context): RecipeApiService {
        return getRetrofit(context).create(RecipeApiService::class.java)
    }
    
    fun getPushAlarmApi(context: Context): PushAlarmApiService {
        return getRetrofit(context).create(PushAlarmApiService::class.java)
    }

    fun getPushFcmTokenApi(context: Context): PushFcmTokenApiService {
        return getRetrofit(context).create(PushFcmTokenApiService::class.java)
    }

    fun getHealthApi(context: Context): HealthApiService {
        return getRetrofit(context).create(HealthApiService::class.java)
    }

    fun getUserInfoApi(context: Context): UserInfoApiService {
        return getRetrofit(context).create(UserInfoApiService::class.java)
    }

    fun getNaverShoppingApi(context: Context): NaverShoppingApiService {
        return getRetrofit(context).create(NaverShoppingApiService::class.java)
    }

    fun getRecipeTrendsApi(context: Context): RecipeTrendsApiService {
        return getRetrofit(context).create(RecipeTrendsApiService::class.java)
    }

    fun getAlarmApi(context: Context): AlarmApiService {
        return getRetrofit(context).create(AlarmApiService::class.java)
    }
}