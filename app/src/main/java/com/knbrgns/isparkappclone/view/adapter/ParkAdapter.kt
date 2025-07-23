package com.knbrgns.isparkappclone.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.knbrgns.isparkappclone.databinding.ItemParkingBinding
import com.knbrgns.isparkappclone.model.Park
import com.knbrgns.isparkappclone.view.viewholder.ParkViewHolder

class ParkAdapter(
    private var parkList: MutableList<Park>,
    private val onItemClick: (Park) -> Unit,
    private val onFavoriteClick: (Park, Int) -> Unit // Position da gönder
) : RecyclerView.Adapter<ParkViewHolder>() {

    companion object {
        const val PAYLOAD_FAVORITE_CHANGED = "FAVORITE_CHANGED"
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ParkViewHolder {
        val binding = ItemParkingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ParkViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ParkViewHolder, position: Int) {
        holder.bind(parkList[position], onItemClick) { park ->
            onFavoriteClick(park, position)
        }
    }

    // 💡 Payload ile sadece favori durumunu güncelle
    override fun onBindViewHolder(
        holder: ParkViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (payloads.isNotEmpty() && payloads.contains(PAYLOAD_FAVORITE_CHANGED)) {
            // Sadece favori ikonunu güncelle, diğer view'lara dokunma
            holder.updateFavoriteOnly(parkList[position].isFavorite)
        } else {
            super.onBindViewHolder(holder, position, payloads)
        }
    }

    override fun getItemCount(): Int = parkList.size

    // ✅ Tek item'i güncelle (scroll pozisyonu korunur)
    fun updateFavorite(position: Int, newFavoriteState: Boolean) {
        if (position in 0 until parkList.size) {
            parkList[position] = parkList[position].copy(isFavorite = newFavoriteState)
            // Sadece o pozisyondaki item'i güncelle
            notifyItemChanged(position, PAYLOAD_FAVORITE_CHANGED)
        }
    }

    // ✅ Favori item'i listeden çıkar (favoriler modunda)
    fun removeFavoriteItem(position: Int) {
        if (position in 0 until parkList.size) {
            parkList.removeAt(position)
            notifyItemRemoved(position)
            // Kalan item'ların pozisyonlarını güncelle
            notifyItemRangeChanged(position, parkList.size - position)
        }
    }

    // ✅ OPTIMIZED UPDATE: DiffUtil benzeri akıllı güncelleme
    fun updateList(newList: List<Park>) {
        println("DEBUG: ParkAdapter.updateList called - New list size: ${newList.size}, Current size: ${parkList.size}")

        // Eğer liste boyutu değişmediyse ve içerik büyük ölçüde aynıysa partial update yap
        if (parkList.size == newList.size && shouldUsePartialUpdate(newList)) {
            updatePartially(newList)
        } else {
            // Tam güncelleme gerekli
            parkList.clear()
            parkList.addAll(newList)
            notifyDataSetChanged()
        }

        println("DEBUG: ParkAdapter.updateList completed")
    }

    private fun shouldUsePartialUpdate(newList: List<Park>): Boolean {
        // İlk 10 item'ın ID'leri aynıysa partial update kullan
        val sampleSize = minOf(10, parkList.size, newList.size)
        for (i in 0 until sampleSize) {
            if (parkList[i].parkID != newList[i].parkID) {
                return false
            }
        }
        return true
    }

    private fun updatePartially(newList: List<Park>) {
        println("DEBUG: Using partial update - checking individual items")
        var changedCount = 0

        for (i in parkList.indices) {
            if (i < newList.size && parkList[i] != newList[i]) {
                parkList[i] = newList[i]
                notifyItemChanged(i, PAYLOAD_FAVORITE_CHANGED)
                changedCount++
            }
        }

        println("DEBUG: Partial update complete - $changedCount items changed")
    }

    // ✅ Current list'i al
    fun getCurrentList(): List<Park> = parkList.toList()
}