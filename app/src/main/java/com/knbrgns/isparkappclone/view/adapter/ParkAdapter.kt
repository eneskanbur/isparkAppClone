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
    private val onFavoriteClick: (Park, Int) -> Unit
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

    // ✅ Payload ile sadece favori durumunu güncelle
    override fun onBindViewHolder(
        holder: ParkViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (payloads.isNotEmpty() && payloads.contains(PAYLOAD_FAVORITE_CHANGED)) {
            // Sadece favori ikonunu güncelle, scroll pozisyonu korunur
            holder.updateFavoriteOnly(parkList[position].isFavorite)
        } else {
            super.onBindViewHolder(holder, position, payloads)
        }
    }

    override fun getItemCount(): Int = parkList.size

    // ✅ TEK ITEM GÜNCELLEMESİ - Scroll pozisyonu korunur
    fun updateFavorite(position: Int, newFavoriteState: Boolean) {
        if (position in 0 until parkList.size) {
            println("DEBUG: Adapter updating single item - Position: $position, New state: $newFavoriteState")
            parkList[position] = parkList[position].copy(isFavorite = newFavoriteState)
            // Sadece o pozisyondaki item'i güncelle - SCROLL KORUNUR
            notifyItemChanged(position, PAYLOAD_FAVORITE_CHANGED)
        }
    }

    // ✅ FAVORİ ITEM ÇIKARMA - Favoriler modunda
    fun removeFavoriteItem(position: Int) {
        if (position in 0 until parkList.size) {
            parkList.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, parkList.size - position)
        }
    }

    // ✅ OPTIMIZE EDİLMİŞ LISTE GÜNCELLEMESİ
    fun updateList(newList: List<Park>) {
        println("DEBUG: ParkAdapter.updateList called - New list size: ${newList.size}, Current size: ${parkList.size}")

        // ✅ AKILLI GÜNCELLEME: Boyut kontrolü
        when {
            // Liste tamamen boşsa
            parkList.isEmpty() && newList.isNotEmpty() -> {
                println("DEBUG: First time loading - using notifyDataSetChanged")
                parkList.clear()
                parkList.addAll(newList)
                notifyDataSetChanged()
            }

            // Liste boyutu aynı - partial update
            parkList.size == newList.size -> {
                println("DEBUG: Same size - using partial update")
                updatePartially(newList)
            }

            // Liste boyutu farklı - tam güncelleme gerekli
            else -> {
                println("DEBUG: Different size - using full update")
                val oldSize = parkList.size
                parkList.clear()
                parkList.addAll(newList)

                // ✅ Boyut farkına göre optimize notify
                if (newList.size > oldSize) {
                    notifyItemRangeChanged(0, oldSize)
                    notifyItemRangeInserted(oldSize, newList.size - oldSize)
                } else {
                    notifyItemRangeChanged(0, newList.size)
                    notifyItemRangeRemoved(newList.size, oldSize - newList.size)
                }
            }
        }

        println("DEBUG: ParkAdapter.updateList completed")
    }

    // ✅ KISMÎ GÜNCELLEME - Scroll pozisyonu korunur
    private fun updatePartially(newList: List<Park>) {
        println("DEBUG: Using partial update - checking individual items")
        var changedCount = 0

        for (i in parkList.indices) {
            if (i < newList.size && parkList[i] != newList[i]) {
                parkList[i] = newList[i]
                // Sadece değişen item'ları güncelle
                notifyItemChanged(i, PAYLOAD_FAVORITE_CHANGED)
                changedCount++
            }
        }

        println("DEBUG: Partial update complete - $changedCount items changed")
    }

    fun getCurrentList(): List<Park> = parkList.toList()
}