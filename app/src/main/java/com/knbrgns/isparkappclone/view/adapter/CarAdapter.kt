package com.knbrgns.isparkappclone.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.knbrgns.isparkappclone.databinding.ItemCarBinding
import com.knbrgns.isparkappclone.model.Car
import com.knbrgns.isparkappclone.view.viewholder.CarViewHolder

class CarAdapter(
    private val carList: List<Car>,
    private val onItemClick: (Car) -> Unit
) : RecyclerView.Adapter<CarViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CarViewHolder {
        val binding = ItemCarBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CarViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: CarViewHolder,
        position: Int
    ) {
        holder.bind(carList[position], onItemClick)
    }

    override fun getItemCount(): Int {
        return carList.size
    }


}