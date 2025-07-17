package com.knbrgns.isparkappclone.view.viewholder

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.knbrgns.isparkappclone.databinding.ItemNewsCampaignBinding
import com.knbrgns.isparkappclone.model.Campaign
import com.knbrgns.isparkappclone.model.News

class CampaignViewHolder(binding: ItemNewsCampaignBinding) : RecyclerView.ViewHolder(binding.root) {

    private val image = binding.ivCardItem
    private val title = binding.tvTitle
    private val shortDescription = binding.tvDetailDescription

    fun bind(item: Campaign, onItemClick: (Campaign) -> Unit) {
        image.setImageDrawable(null)
        title.text = ""
        shortDescription.text = ""

        Glide.with(itemView.context).load(item.imageUrl).into(image)
        title.text = item.title
        shortDescription.text = item.descriptionShort

        itemView.setOnClickListener {
            onItemClick.invoke(item)
        }
    }
}
