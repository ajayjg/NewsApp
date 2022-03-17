package com.daily.news.module.news_feed

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daily.news.R
import com.daily.news.module.news_feed.data_source.NewsFeedRepository
import com.daily.news.module.news_feed.model.NewsResponse
import com.daily.news.network.Resource
import com.daily.news.utils.isNetworkConnected
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class NewsFeedViewModel @Inject constructor(
    val context: Application,
    val dataSource: NewsFeedRepository
) : ViewModel() {

    val newsMutableLiveData: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    var newsPageCount = 1
    var newsResponse: NewsResponse? = null

    fun getNews() {
        if (isNetworkConnected(context)) {
            newsMutableLiveData.postValue(Resource.Loading())
            viewModelScope.launch(Dispatchers.IO) {
                kotlin.runCatching {
                    val response = dataSource.getNews(newsPageCount)
                    val newsResponse = convertNewsResponse(response)
                    newsMutableLiveData.postValue(newsResponse)
                }.onFailure {
                    newsMutableLiveData.postValue(
                        Resource.Error(
                            it.message ?: context.getString(R.string.something_went_wrong)
                        )
                    )
                }
            }
        } else {
            newsMutableLiveData.postValue(
                Resource.Error(context.getString(R.string.check_network))
            )
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