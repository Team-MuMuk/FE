package com.example.mumuk.data.repository

import com.example.mumuk.data.model.Blog
import com.example.mumuk.R

object BlogRepository {
    fun getBlogList(): List<Blog> {
        return listOf(
            Blog(1, "블로그 제목 1", "블로그 본문 1블로그 본문 1블로그 본문 1블로그 본문 1블로그 본문 1블로그 본문 1", R.drawable.bg_mosaic),
            Blog(2, "블로그 제목 2", "블로그 본문 2", R.drawable.bg_mosaic),
            Blog(1, "블로그 제목 1", "블로그 본문 1블로그 본문 1블로그 본문 1블로그 본문 1블로그 본문 1블로그 본문 1블로그 본문 1블로그 본문 1블로그 본문 1블로그 본문 1블로그 본문 1블로그 본문 1블로그 본문 1블로그 본문 1블로그 본문 1블로그 본문 1블로그 본문 1블로그 본문 1블로그 본문 1블로그 본문 1블로그 본문 1블로그 본문 1블로그 본문 1블로그 본문 1블로그 본문 1블로그 본문 1블로그 본문 1블로그 본문 1블로그 본문 1블로그 본문 1블로그 본문 1블로그 본문 1블로그 본문 1블로그 본문 1블로그 본문 1", R.drawable.bg_mosaic),
            Blog(2, "블로그 제목 2", "블로그 본문 2", R.drawable.bg_mosaic),
            Blog(1, "블로그 제목 1", "블로그 본문 1", R.drawable.bg_mosaic),
            Blog(2, "블로그 제목 2", "블로그 본문 2", R.drawable.bg_mosaic)
        )
    }
}