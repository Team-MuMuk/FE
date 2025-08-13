package com.example.mumuk.data.repository

import android.util.Log
import com.example.mumuk.data.api.CategoryRecipeApiService
import com.example.mumuk.data.api.UserRecipeApiService
import com.example.mumuk.data.model.Recipe
import com.example.mumuk.data.model.bookmark.LikedRecipeResponse
import com.example.mumuk.data.model.category.CategoryRecipe
import com.example.mumuk.data.model.recipe.ClickLikeRequest
import kotlinx.coroutines.suspendCancellableCoroutine
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

enum class RecipeCategory { WEIGHT, HEALTH, ALL }

class BookmarkRecipeRepository(
    private val categoryApi: CategoryRecipeApiService,
    private val userRecipeApiService: UserRecipeApiService
) {
    private suspend fun <T> Call<T>.await(): T =
        suspendCancellableCoroutine { cont ->
            enqueue(object : Callback<T> {
                override fun onResponse(call: Call<T>, resp: Response<T>) {
                    val body = resp.body()
                    if (resp.isSuccessful && body != null) cont.resume(body) else
                        cont.resumeWithException(
                            IllegalStateException("HTTP ${resp.code()}: ${resp.errorBody()?.string()}")
                        )
                }
                override fun onFailure(call: Call<T>, t: Throwable) { cont.resumeWithException(t) }
            })
            cont.invokeOnCancellation { cancel() }
        }
    private suspend fun <T> Call<T>.awaitOrNull(): T? = try { await() } catch (_: Throwable) { null }

    private data class MyLiked(
        val id: Long,
        val name: String,
        val imageUrl: String?
    )

    private suspend fun getAllMyLiked(): List<MyLiked> {
        val acc = mutableListOf<MyLiked>()
        var page = 1
        val size = 50
        while (true) {

            val res: LikedRecipeResponse =
                userRecipeApiService.getLikedRecipes(page, size).awaitOrNull() ?: break

            val d = res.data ?: break

            d.likedRecipes.forEach { item ->
                acc += MyLiked(
                    id = item.recipeId,
                    name = item.name,
                    imageUrl = item.imageUrl
                )
            }
            if (!d.hasNext) break
            page += 1
        }
        Log.d("BOOKMARK_REPO", "myLiked size=${acc.size}")
        return acc
    }

    private suspend fun getMyLikedIds(): Set<Long> =
        getAllMyLiked().map { it.id }.toSet()

    private suspend fun fetchCategories(keys: List<String>): List<CategoryRecipe> {
        val all = mutableListOf<CategoryRecipe>()
        for (k in keys) {
            val res = categoryApi.getRecommendedRecipes(k).await()
            all += (res.data ?: emptyList())
        }
        return all
    }

    private fun mapToUi(list: List<CategoryRecipe>) = list.map {
        Recipe(
            id = it.recipeId,
            img = null,
            title = it.name,
            isLiked = true,
            recipeImageUrl = it.imageUrl
        )
    }

    private suspend fun loadCategoryLikedOnly(keys: List<String>): List<Recipe> {
        val myLiked = try { getMyLikedIds() } catch (_: Throwable) { emptySet() }
        val list = try { fetchCategories(keys) } catch (_: Throwable) { emptyList() }

        val filtered = if (myLiked.isEmpty()) emptyList() else list.filter { it.recipeId in myLiked }

        Log.d("BOOKMARK_REPO", "keys=$keys, total=${list.size}, likedIntersect=${filtered.size}")
        return mapToUi(filtered)
    }

    suspend fun getWeightRecipes(): List<Recipe> =
        loadCategoryLikedOnly(CategoryKeys.WEIGHT_ALL)

    suspend fun getHealthRecipes(): List<Recipe> =
        loadCategoryLikedOnly(CategoryKeys.HEALTH_ALL)

    suspend fun getAllBookmarkedRecipes(): List<Recipe> {
        val liked: List<MyLiked> = try { getAllMyLiked() } catch (_: Throwable) { emptyList() }
        return liked.map { m ->
            Recipe(
                id = m.id,
                img = null,
                title = m.name,
                isLiked = true,
                recipeImageUrl = m.imageUrl
            )
        }
    }
    suspend fun toggleLike(recipeId: Long) {
        userRecipeApiService.clickLike(ClickLikeRequest(recipeId)).await()
    }
}
