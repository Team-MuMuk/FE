package com.example.mumuk.data.api

import com.example.mumuk.data.model.search.RecentSearch
import com.example.mumuk.data.model.search.RecentSearchResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.HTTP
import retrofit2.http.POST
import retrofit2.http.Query

interface RecentSearchApiService {
    @POST("/api/search/recent-searches")
    fun saveRecentSearch(@Query("keyword") keyword: String): Call<RecentSearchResponse>

    @GET("/api/search/recent-searches")
    fun getRecentSearches(): Call<RecentSearchResponse>

    @HTTP(method = "DELETE", path = "/api/search/recent-searches", hasBody = true)
    fun deleteRecentSearch(@Body recentSearch: RecentSearch): Call<RecentSearchResponse>
}