package com.daily.news.module.news_details

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.daily.news.databinding.ActivityNewsDetailsBinding
import com.daily.news.module.news_feed.model.Article
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NewsDetailsActivity : AppCompatActivity() {

    private val mViewModel by viewModels<NewsDetailsViewModel>()
    lateinit var binding: ActivityNewsDetailsBinding

    companion object {
        private const val EXTRA_ARTICLE_DATA = "EXTRA_ARTICLE_DATA"

        fun getIntent(context: Context, article: Article): Intent {
            return Intent(context, NewsDetailsActivity::class.java).apply {
                putExtra(EXTRA_ARTICLE_DATA, article)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewsDetailsBinding.inflate(layoutInflater)
        binding.lifecycleOwner = this
        binding.mViewModel = mViewModel
        setContentView(binding.root)
        val article: Article? = intent.getParcelableExtra(EXTRA_ARTICLE_DATA) as? Article
        article?.let {
            mViewModel.initialize(it)
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}