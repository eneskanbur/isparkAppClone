package com.knbrgns.isparkappclone.view.viewholder

import androidx.recyclerview.widget.RecyclerView
import com.knbrgns.isparkappclone.databinding.ItemParkingBinding
import com.knbrgns.isparkappclone.model.Park

class ParkViewHolder(binding: ItemParkingBinding) : RecyclerView.ViewHolder(binding.root) {

    private var parkName = binding.tvParkName
    private val availableSpot = binding.tvAvailableSpots
    private val adress = binding.tvParkAddress
    private val distance = binding.tvDistance
    private val pricing = binding.tvPricing
    private val navigate = binding.btnNavigate
    private val freeMinute = binding.tvFreeMinutes

    fun bind(item: Park, onItemClick: (Park) -> Unit) {
        parkName.text = item.parkName
        availableSpot.text = "${item.emptyCapacity} boş"
        adress.text = item.district ?: item.address
        distance.text = "Null"
        pricing.text = "Saatlik ${item.tariff} ₺" ?: "Null"
        freeMinute.text = "İlk ${item.freeTime} dk ücretsiz"
        navigate.setOnClickListener { onItemClick.invoke(item) }
    }


}
