package com.knbrgns.isparkappclone.view.viewholder

import android.content.res.ColorStateList
import androidx.recyclerview.widget.RecyclerView
import com.knbrgns.isparkappclone.R
import com.knbrgns.isparkappclone.databinding.ItemParkingBinding
import com.knbrgns.isparkappclone.model.Park

class ParkViewHolder(private val binding: ItemParkingBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(
        item: Park,
        isFavorite: Boolean,
        onItemClick: (Park) -> Unit,
        onFavoriteClick: (Park) -> Unit
    ) {
        binding.tvParkName.text = item.parkName
        binding.tvAvailableSpots.text = "${item.emptyCapacity} boş"
        binding.tvParkAddress.text = item.district ?: item.address
        binding.tvDistance.text = "250 m"  // Mock
        binding.tvPricing.text = "Saatlik ${item.tariff ?: "15"} ₺"
        binding.tvFreeMinutes.text = "İlk ${item.freeTime} dk ücretsiz"

        binding.btnFavorite.apply {
            val heartIcon =
                if (isFavorite) R.drawable.ic_heart_filled else R.drawable.ic_heart
            setIconResource(heartIcon)

            val color = if (isFavorite) R.color.red else R.color.textSecondary
            iconTint = ColorStateList.valueOf(itemView.context.getColor(color))

            setOnClickListener { onFavoriteClick(item) }
        }

        binding.btnNavigate.setOnClickListener { onItemClick(item) }
        binding.cardParking.setOnClickListener { onItemClick(item) }
    }
}



