package com.daily.news.utils

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.daily.news.module.news_feed.model.Article
import kotlinx.android.synthetic.main.item_news_article.view.*

@BindingAdapter("setImageUrl")
fun setImageUrl(imageView: ImageView, url: String) {
    Glide.with(imageView.context).load(url).into(imageView)
}