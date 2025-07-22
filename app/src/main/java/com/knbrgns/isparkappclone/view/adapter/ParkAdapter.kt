package com.knbrgns.isparkappclone.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.knbrgns.isparkappclone.databinding.ItemParkingBinding
import com.knbrgns.isparkappclone.model.Park
import com.knbrgns.isparkappclone.view.viewholder.ParkViewHolder

class ParkAdapter(
    private val parkList: List<Park>,
    private val onItemClick: (Park) -> Unit,
    private val onFavoriteClick: (Park) -> Unit
) : RecyclerView.Adapter<ParkViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ParkViewHolder {
        val binding = ItemParkingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ParkViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: ParkViewHolder,
        position: Int
    ) {
        holder.bind(parkList[position],onItemClick,onFavoriteClick)
    }

    override fun getItemCount(): Int {
        return parkList.size
    }
}