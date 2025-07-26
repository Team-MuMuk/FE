package com.example.mumuk.data.api

import com.example.mumuk.data.model.search.PopularKeywordResponse
import retrofit2.Call
import retrofit2.http.GET

interface PopularKeywordApiService {
    @GET("/api/search/search-trends")
    fun getPopularKeywords(): Call<PopularKeywordResponse>
}