package com.daily.news.utils

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.daily.news.module.news_feed.model.Article
import kotlinx.android.synthetic.main.item_news_article.view.*

//@BindingAdapter("setAdapter")
//fun bindItemViewModels(recyclerView: RecyclerView, itemViewModels: List<Article>?) {
//    val adapter = getOrCreateAdapter(recyclerView)
//    adapter.updateItems(itemViewModels)
//}

//private fun getOrCreateAdapter(recyclerView: RecyclerView): Binda {
//    return if (recyclerView.adapter != null && recyclerView.adapter is BindableRecyclerViewAdapter) {
//        recyclerView.adapter as BindableRecyclerViewAdapter
//    } else {
//        val bindableRecyclerAdapter = BindableRecyclerViewAdapter()
//        recyclerView.adapter = bindableRecyclerAdapter
//        bindableRecyclerAdapter
//    }
//}

@BindingAdapter("setImageUrl")
fun setImageUrl(imageView: ImageView, url: String) {
    Glide.with(imageView.context).load(url).into(imageView)
}