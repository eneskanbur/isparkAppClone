package com.knbrgns.isparkappclone.view.viewholder

import androidx.recyclerview.widget.RecyclerView
import com.knbrgns.isparkappclone.databinding.ItemCarBinding
import com.knbrgns.isparkappclone.model.Car

class CarViewHolder(val binding: ItemCarBinding): RecyclerView.ViewHolder(binding.root) {

    fun bind(car: Car, onItemClick: (Car) -> Unit){
        binding.tvCarName.text = car.carName
        binding.tvCarType.text = car.carType
        binding.tvDebt.text = car.debt.toString()
        binding.tvLicensePlate.text = car.plate
        binding.tvLastParkingLocation.text = car.parkingLocation

        binding.btnCarDetails.setOnClickListener {
            onItemClick(car)
        }

        binding.cardCar.setOnClickListener {
            onItemClick(car)
        }
    }

}