package com.daily.news.module.news_feed.data_source

import com.daily.news.module.news_feed.model.NewsResponse
import com.daily.news.network.Resource
import retrofit2.Response
import javax.inject.Inject

class NewsFeedRepository @Inject constructor(val persistence : NewsFeedPersistence, val service : NewsFeedService) {

    suspend fun getNews(pageNumber : Int) : Response<NewsResponse> {
        return service.getNews(pageNumber)
    }
}