package com.androiddevs.mvvmnewsapp.api

import com.daily.news.module.news_feed.model.NewsResponse
import com.daily.news.utils.Constants
import com.daily.news.utils.Constants.Companion.STARTING_PAGE_INDEX
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface APIService {

    @GET("v2/everything")
    suspend fun getNews(
        @Query("q")
        query: String = "Covid",
        @Query("page")
        pageNumber: Int = STARTING_PAGE_INDEX,
        @Query("apiKey")
        apiKey: String = Constants.API_KEY
    ): Response<NewsResponse>

}