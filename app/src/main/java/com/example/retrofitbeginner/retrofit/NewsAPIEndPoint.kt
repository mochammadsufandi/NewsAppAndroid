package com.example.retrofitbeginner.retrofit

import com.example.retrofitbeginner.model.NewsResponse
import com.example.retrofitbeginner.utils.Constants
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsAPIEndPoint {

    @GET("v2/top-headlines")
    fun getHeadlinesNews(
        @Query("country") countryCode: String = "us",
        @Query("apiKey") apiKey: String = Constants.API_KEY,
        @Query("page") page: Int,
        @Query("pageSize") pageSize: Int
    ): Call<NewsResponse>

    @GET("v2/everything")
    fun getEverythingNews(
        @Query("q") searchQuery: String,
        @Query("apiKey") apiKey: String = Constants.API_KEY,
        @Query("page") page: Int,
        @Query("pageSize") pageSize: Int
    ): Call<NewsResponse>
}
