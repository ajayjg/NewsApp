package com.daily.news.module.news_details

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.daily.news.module.news_feed.model.Article
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class NewsDetailsViewModel @Inject constructor() : ViewModel() {

    val articleImage = MutableLiveData("")
    val articleTitle = MutableLiveData("")
    val articleDescription = MutableLiveData("")
    val contentData = MutableLiveData("")
    val sourceName = MutableLiveData("")
    val publishedDate = MutableLiveData("")

    fun initialize(article: Article) {
        article.apply {
            articleImage.value = urlToImage
            articleTitle.value = title
            articleDescription.value = description
            contentData.value = content
            sourceName.value = source.name
            publishedDate.value = publishedAt
        }
    }
}