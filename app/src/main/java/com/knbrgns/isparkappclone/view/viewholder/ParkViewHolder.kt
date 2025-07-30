package com.knbrgns.isparkappclone.view.viewholder

import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.knbrgns.isparkappclone.R
import com.knbrgns.isparkappclone.databinding.ItemParkingBinding
import com.knbrgns.isparkappclone.model.Park

class ParkViewHolder(
    val binding: ItemParkingBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(item: Park, onItemClick: (Park) -> Unit, onFavoriteClick: (Park) -> Unit) {
        binding.tvParkName.text = item.parkName
        binding.tvAvailableSpots.text = "${item.emptyCapacity} boş"
        binding.tvParkAddress.text = item.district ?: item.address
        binding.tvParkType.text = item.parkType
        if (item.freeTime != 0){
            binding.tvFreeMinutes.text = "İlk ${item.freeTime} dk ücretsiz"
        }else{
            binding.tvFreeMinutes.text = "Ücretsiz dakika avantajı yok"
           /* binding.tvFreeMinutes.visibility = View.GONE
            binding.ivFreeMinutes.visibility = View.GONE*/
        }

        updateFavoriteIcon(item.isFavorite)

        binding.cardParking.setOnClickListener { onItemClick(item) }
        binding.btnNavigate.setOnClickListener { onItemClick(item) }
        binding.btnFavorite.setOnClickListener { onFavoriteClick(item) }
    }

    fun updateFavoriteOnly(isFavorite: Boolean) {
        updateFavoriteIcon(isFavorite)
    }

    private fun updateFavoriteIcon(isFavorite: Boolean) {
        if (isFavorite) {
            binding.btnFavorite.setIconResource(R.drawable.ic_heart_filled)
            binding.btnFavorite.iconTint = ContextCompat.getColorStateList(
                itemView.context, R.color.red
            )
        } else {
            binding.btnFavorite.setIconResource(R.drawable.ic_heart)
            binding.btnFavorite.iconTint = ContextCompat.getColorStateList(
                itemView.context, R.color.textSecondary
            )
        }
    }
}