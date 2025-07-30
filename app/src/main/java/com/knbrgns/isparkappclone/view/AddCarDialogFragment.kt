package com.knbrgns.isparkappclone.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.DialogFragment
import com.knbrgns.isparkappclone.databinding.FragmentAddCarDialogBinding
import com.knbrgns.isparkappclone.model.Car

class AddCarDialogFragment(
    private val onCarAdded: (Car) -> Unit
) : DialogFragment() {

    private var _binding: FragmentAddCarDialogBinding? = null
    private val binding get() = _binding!!

    private val carTypes = listOf(
        "Sedan",
        "Hatchback",
        "SUV",
        "Coupe",
        "Station Wagon",
        "Pickup"
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddCarDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupDialog()
        setupDropdown()
        setupClickListeners()
    }

    private fun setupDialog() {
        dialog?.window?.let { window ->
            window.setBackgroundDrawableResource(android.R.color.transparent)

            val width = (resources.displayMetrics.widthPixels * 0.92).toInt()
            window.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)
        }

        dialog?.setCanceledOnTouchOutside(false)
    }

    private fun setupDropdown() {
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            carTypes
        )

        binding.actvCarType.setAdapter(adapter)

        binding.actvCarType.setText(carTypes[0], false)

        binding.actvCarType.setOnClickListener {
            binding.actvCarType.showDropDown()
        }
    }

    private fun setupClickListeners() {
        binding.btnCancel.setOnClickListener {
            dismiss()
        }

        binding.btnSave.setOnClickListener {
            saveCar()
        }
    }

    private fun saveCar() {
        val carName = binding.etCarName.text.toString().trim()
        val plate = binding.etPlate.text.toString().trim().uppercase()
        val carType = binding.actvCarType.text.toString()

        if (!validateInputs(carName, plate, carType)) {
            return
        }

        val newCar = Car(
            id = 0,
            carName = carName,
            plate = formatPlate(plate),
            carType = carType,
            parkingLocation = "Henüz park edilmemiş",
            debt = 0.0
        )

        onCarAdded(newCar)
        dismiss()
    }

    private fun validateInputs(carName: String, plate: String, carType: String): Boolean {
        var isValid = true

        if (carName.isEmpty()) {
            binding.tilCarName.error = "Araç adı gereklidir"
            isValid = false
        } else if (carName.length < 2) {
            binding.tilCarName.error = "Araç adı en az 2 karakter olmalı"
            isValid = false
        } else {
            binding.tilCarName.error = null
        }

        val cleanPlate = plate.replace(" ", "")
        if (plate.isEmpty()) {
            binding.tilPlate.error = "Plaka gereklidir"
            isValid = false
        } else if (cleanPlate.length < 7 || cleanPlate.length > 8) {
            binding.tilPlate.error = "Geçerli bir plaka giriniz"
            isValid = false
        } else {
            binding.tilPlate.error = null
        }

        if (carType.isEmpty() || !carTypes.contains(carType)) {
            binding.tilCarType.error = "Geçerli bir araç tipi seçiniz"
            isValid = false
        } else {
            binding.tilCarType.error = null
        }

        return isValid
    }

    private fun formatPlate(plate: String): String {
        val cleanPlate = plate.replace(" ", "")
        return if (cleanPlate.length >= 7) {
            "${cleanPlate.substring(0, 2)} ${cleanPlate.substring(2, cleanPlate.length - 3)} ${cleanPlate.substring(cleanPlate.length - 3)}"
        } else {
            plate
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}