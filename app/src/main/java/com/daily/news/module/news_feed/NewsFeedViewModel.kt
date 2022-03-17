package com.daily.news.module.news_feed

import android.app.Application
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daily.news.R
import com.daily.news.module.news_feed.data_source.NewsFeedDataSource
import com.daily.news.module.news_feed.model.NewsResponse
import com.daily.news.network.Resource
import com.daily.news.utils.isNetworkConnected
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class NewsFeedViewModel @Inject constructor(
    val context: Application,
    val dataSource: NewsFeedDataSource
) : ViewModel() {

    val isProgressVisible = MutableLiveData<Int>(View.GONE)
    val loaderMessage = MutableLiveData<String>(context.getString(R.string.loading_news))
    val loaderMessageVisibility = MutableLiveData(View.VISIBLE)
    val news: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    var newsPageCount = 1
    var newsResponse: NewsResponse? = null
    fun getNews() {
        isProgressVisible.value = View.VISIBLE
        if (isNetworkConnected(context)) {
            viewModelScope.launch(Dispatchers.IO) {
                kotlin.runCatching {
                    val response = dataSource.getNews(newsPageCount)
                    isProgressVisible.postValue(View.GONE)
                    val newsResponse = convertNewsResponse(response)
                    news.postValue(newsResponse)
                }.onFailure {
                    news.postValue(Resource.Error(context.getString(R.string.something_went_wrong)))
                }
            }
        } else {
            loaderMessage.value = context.getString(R.string.check_network)
            isProgressVisible.postValue(View.GONE)
        }
    }

    private fun convertNewsResponse(response: Response<NewsResponse>): Resource<NewsResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                newsPageCount++
                if (newsResponse == null) {
                    newsResponse = resultResponse
                } else {
                    val oldArticles = newsResponse?.articles
                    val newArticles = resultResponse.articles
                    oldArticles?.addAll(newArticles)
                }
                loaderMessageVisibility.postValue(View.GONE)
                return Resource.Success(newsResponse ?: resultResponse)
            }
        }
        return if (response.message().isNotEmpty()) {
            Resource.Error(response.message())
        } else {
            Resource.Error(context.getString(R.string.something_went_wrong))
        }
    }
}