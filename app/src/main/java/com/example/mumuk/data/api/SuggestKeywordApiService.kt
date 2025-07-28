package com.example.mumuk.data.api

import com.example.mumuk.data.model.search.SuggestKeywordResponse
import retrofit2.Call
import retrofit2.http.GET

interface SuggestKeywordApiService {
    @GET("/api/search/recommended-keywords")
    fun getSuggestKeywords(): Call<SuggestKeywordResponse>
}