package com.daily.news.module.news_feed.data_source

import com.androiddevs.mvvmnewsapp.api.APIService
import javax.inject.Inject

class NewsFeedService @Inject constructor() {

    @Inject lateinit var apiService : APIService

    suspend fun getNews(pageNumber: Int) = apiService.getNews("Covid",pageNumber)

}