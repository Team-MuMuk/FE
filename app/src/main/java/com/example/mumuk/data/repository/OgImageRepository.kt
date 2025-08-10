package com.example.mumuk.data.repository

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup

object OgImageRepository {
    private const val USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/108.0.0.0 Safari/537.36"

    suspend fun fetchOgImage(url: String): String? {
        return withContext(Dispatchers.IO) {
            try {
                if (url.contains("blog.naver.com")) {
                    Log.d("OgImageRepository", "Fetching Naver blog main page: $url")
                    val mainDoc = Jsoup.connect(url)
                        .userAgent(USER_AGENT)
                        .get()

                    val iframe = mainDoc.getElementById("mainFrame")
                    val iframeSrc = iframe?.attr("src")

                    if (iframeSrc != null) {
                        val contentUrl = "https://blog.naver.com$iframeSrc"
                        Log.d("OgImageRepository", "Fetching content from iframe: $contentUrl")
                        val contentDoc = Jsoup.connect(contentUrl)
                            .userAgent(USER_AGENT)
                            .referrer(url)
                            .get()

                        val ogImage = contentDoc.select("meta[property=og:image]").firstOrNull()
                        val imageUrl = ogImage?.attr("content")
                        Log.d("OgImageRepository", "Found OG image URL in iframe: $imageUrl")
                        return@withContext imageUrl
                    } else {
                        Log.w("OgImageRepository", "mainFrame not found for Naver blog: $url")
                    }
                }
                Log.d("OgImageRepository", "Fetching OG image for general URL: $url")
                val doc = Jsoup.connect(url)
                    .userAgent(USER_AGENT)
                    .get()
                val ogImage = doc.select("meta[property=og:image]").firstOrNull()
                val imageUrl = ogImage?.attr("content")
                Log.d("OgImageRepository", "Found OG image URL: $imageUrl")
                imageUrl

            } catch (e: Exception) {
                Log.e("OgImageRepository", "Error fetching OG image for $url", e)
                null
            }
        }
    }
}