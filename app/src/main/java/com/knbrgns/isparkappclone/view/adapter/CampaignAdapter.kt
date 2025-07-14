package com.knbrgns.isparkappclone.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.knbrgns.isparkappclone.databinding.ItemNewsCampaignBinding
import com.knbrgns.isparkappclone.model.Campaign
import com.knbrgns.isparkappclone.view.viewholder.CampaignViewHolder

class CampaignAdapter(
    private val campaignList: List<Campaign>,
    private val onItemClick: (Campaign) -> Unit
): RecyclerView.Adapter<CampaignViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CampaignViewHolder {
        val binding = ItemNewsCampaignBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CampaignViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: CampaignViewHolder,
        position: Int
    ) {
        holder.bind(campaignList[position], onItemClick)
    }

    override fun getItemCount(): Int {
        return campaignList.size
    }
}