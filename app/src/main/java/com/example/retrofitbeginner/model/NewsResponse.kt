package com.example.retrofitbeginner.model

data class NewsResponse(
    val articles: List<Article>,
    val status: String,
    val totalResults: Int
)