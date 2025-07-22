package com.knbrgns.isparkappclone.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.knbrgns.isparkappclone.databinding.ItemParkingBinding
import com.knbrgns.isparkappclone.model.Park
import com.knbrgns.isparkappclone.view.viewholder.ParkViewHolder

class ParkAdapter(
    private val onItemClick: (Park) -> Unit,
    private val onFavoriteClick: (Park) -> Unit
) : RecyclerView.Adapter<ParkViewHolder>() {

    private var parkList: List<Park> = emptyList()
    private var favoriteIds: Set<Int> = emptySet()


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ParkViewHolder {
        val binding = ItemParkingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ParkViewHolder(binding)
    }

    fun updateData(newParks: List<Park>, newFavorites: List<Int>) {
        this.parkList = newParks
        this.favoriteIds = newFavorites.toSet()
        notifyDataSetChanged()  // ✅ Basit güncelleme
    }

    override fun onBindViewHolder(
        holder: ParkViewHolder,
        position: Int
    ) {
        val park = parkList[position]
        val isFavorite = favoriteIds.contains(park.parkID)

        holder.bind(park, isFavorite, onItemClick, onFavoriteClick)
    }

    override fun getItemCount(): Int {
        return parkList.size
    }
}