package com.knbrgns.isparkappclone.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.knbrgns.isparkappclone.databinding.ItemNewsCampaignBinding
import com.knbrgns.isparkappclone.model.News
import com.knbrgns.isparkappclone.view.viewholder.NewsViewHolder

class NewsAdapter(
    private val newsList: List<News>,
    private val onItemClick: (News) -> Unit
) : RecyclerView.Adapter<NewsViewHolder>(){

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): NewsViewHolder {
        val binding = ItemNewsCampaignBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return NewsViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: NewsViewHolder,
        position: Int
    ) {
        holder.bind(newsList[position],onItemClick)
    }

    override fun getItemCount(): Int {
        return newsList.size
    }
}