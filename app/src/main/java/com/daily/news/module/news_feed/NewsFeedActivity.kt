package com.daily.news.module.news_feed

import android.os.Bundle
import android.widget.AbsListView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.androiddevs.mvvmnewsapp.adapters.NewsAdapter
import com.daily.news.databinding.ActivityNewsFeedBinding
import com.daily.news.module.news_details.NewsDetailsActivity
import com.daily.news.network.Resource
import com.daily.news.utils.Constants
import com.daily.news.utils.Constants.Companion.QUERY_PAGE_SIZE
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NewsFeedActivity : AppCompatActivity() {

    lateinit var newsAdapter: NewsAdapter

    private val mViewModel by viewModels<NewsFeedViewModel>()
    lateinit var binding: ActivityNewsFeedBinding

    var isLoading = false
    var isLastPage = false
    var isScrolling = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewsFeedBinding.inflate(layoutInflater)
        binding.apply {
            lifecycleOwner = this@NewsFeedActivity
            mViewModel = mViewModel
        }
        setContentView(binding.root)
        setupNewsFeed()
        mViewModel.getNews()
    }

    private fun setupNewsFeed() {
        newsAdapter = NewsAdapter()
        newsAdapter.setOnItemClickListener {
            startActivity(NewsDetailsActivity.getIntent(this, article = it))
        }
        binding.rvNfList.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(this@NewsFeedActivity)
            addOnScrollListener(this@NewsFeedActivity.scrollListener)
        }
    }

    override fun onStart() {
        super.onStart()
        mViewModel.news.observe(this) { response ->
            when (response) {
                is Resource.Success -> {
                    response.data?.let { newsResponse ->
                        newsAdapter.differ.submitList(newsResponse.articles.toList())
                        val totalPages = newsResponse.totalResults / QUERY_PAGE_SIZE + 2
                        isLastPage = mViewModel.newsPageCount == totalPages
                    }
                }
                is Resource.Error -> {
                    response.message?.let { message ->
                        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    private val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
            val visibleItemCount = layoutManager.childCount
            val totalItemCount = layoutManager.itemCount

            val isNotLoadingAndNotLastPage = !isLoading && !isLastPage
            val isAtLastItem = firstVisibleItemPosition + visibleItemCount >= totalItemCount
            val isNotAtBeginning = firstVisibleItemPosition >= 0
            val isTotalMoreThanVisible = totalItemCount >= QUERY_PAGE_SIZE
            val shouldPaginate = isNotLoadingAndNotLastPage && isAtLastItem && isNotAtBeginning &&
                    isTotalMoreThanVisible && isScrolling
            if (shouldPaginate) {
                mViewModel.getNews()
                isScrolling = false
            } else {
                binding.rvNfList.setPadding(0, 0, 0, 0)
            }
        }

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                isScrolling = true
            }
        }
    }
}