package com.daily.news.module.news_feed

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AbsListView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.androiddevs.mvvmnewsapp.adapters.NewsAdapter
import com.daily.news.R
import com.daily.news.databinding.ActivityNewsFeedBinding
import com.daily.news.module.news_details.NewsDetailsActivity
import com.daily.news.network.Resource
import com.daily.news.utils.Constants.Companion.QUERY_PAGE_SIZE
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NewsFeedActivity : AppCompatActivity() {

    companion object {
        const val TAG = "NewsFeedActivity"
    }

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
        mViewModel.newsMutableLiveData.observe(this) { response ->
            when (response) {
                is Resource.Success -> {
                    response.data?.let { newsResponse ->
                        newsAdapter.differ.submitList(newsResponse.articles.toList())
                        val totalPages = newsResponse.totalResults / QUERY_PAGE_SIZE + 2
                        isLastPage = mViewModel.newsPageCount == totalPages
                        binding.pbNfPaginatedLoader.visibility = View.GONE
                        if(newsAdapter.itemCount == 0){
                            binding.tvNfStatus.visibility = View.VISIBLE
                            binding.tvNfStatus.text = resources.getString(R.string.no_news)
                        } else {
                            binding.tvNfStatus.visibility = View.GONE
                        }
                    }
                }
                is Resource.Error -> {
                    response.message?.let { message ->
                        binding.apply {
                            pbNfPaginatedLoader.visibility = View.GONE
                            tvNfStatus.visibility = View.VISIBLE
                            tvNfStatus.text = message
                        }
                        Log.d(TAG, message)
                    }
                }
                is Resource.Loading -> {
                    binding.apply {
                        tvNfStatus.visibility = View.VISIBLE
                        tvNfStatus.text = resources.getString(R.string.loading_news)
                        pbNfPaginatedLoader.visibility = View.VISIBLE
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
                if (totalItemCount < 100) {//Hardcoded to 100 because trial API only serves 100 news
                    mViewModel.getNews()
                    isScrolling = false
                }
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