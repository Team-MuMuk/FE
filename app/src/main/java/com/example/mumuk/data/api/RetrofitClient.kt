package com.example.mumuk.data.api

import android.content.Context
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "https://api.mumuk.site"
    private var retrofit: Retrofit? = null

    private fun getRetrofit(context: Context): Retrofit {
        if (retrofit == null) {
            // 1. HttpLoggingInterceptor 추가
            val logging = HttpLoggingInterceptor().apply {
                // 개발 중엔 BODY, 운영 배포 땐 필요한 경우 INFO/ERROR로
                level = HttpLoggingInterceptor.Level.BODY
            }
            val client = OkHttpClient.Builder()
                .addInterceptor(AuthInterceptor(context))
                .addInterceptor(logging) // 2. loggingInterceptor 추가
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
}