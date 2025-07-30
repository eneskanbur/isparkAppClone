package com.knbrgns.isparkappclone.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.knbrgns.isparkappclone.R
import com.knbrgns.isparkappclone.databinding.FragmentCarDetailBinding
import com.knbrgns.isparkappclone.model.Car
import com.knbrgns.isparkappclone.view.viewmodel.CarDetailViewModel
import java.text.SimpleDateFormat
import java.util.*

class CarDetailFragment : Fragment() {

    private var _binding: FragmentCarDetailBinding? = null
    private val binding get() = _binding!!

    private lateinit var car: Car
    private val args: CarDetailFragmentArgs by navArgs()
    private val viewModel: CarDetailViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCarDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        car = args.car
        setupCarInfo()
        setupClickListeners()
        observeViewModel()
    }

    private fun setupCarInfo() {
        with(binding) {
            tvCarName.text = car.carName
            tvLicensePlate.text = car.plate
            tvCarType.text = car.carType
            tvLastParkingLocation.text = car.parkingLocation ?: "Henüz park edilmemiş"

            if (car.debt > 0) {
                tvDebtAmount.text = "${car.debt} ₺"
                cardDebt.setCardBackgroundColor(
                    resources.getColor(R.color.red, null)
                )
                tvDebtStatus.text = "Ödenmemiş Borç"
                ivDebtIcon.setImageResource(R.drawable.ic_debt)
            } else {
                tvDebtAmount.text = "0 ₺"
                cardDebt.setCardBackgroundColor(
                    resources.getColor(R.color.colorPrimary, null)
                )
                tvDebtStatus.text = "Borç Yok"
                ivDebtIcon.setImageResource(R.drawable.ic_check)
            }

            val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale("tr", "TR"))
            tvRegistrationDate.text = dateFormat.format(Date())
        }
    }

    private fun setupClickListeners() {
        binding.btnDeleteCar.setOnClickListener {
            showDeleteConfirmationDialog()
        }

        binding.btnEditCar.setOnClickListener {
            Toast.makeText(context, "Düzenleme özelliği yakında...", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showDeleteConfirmationDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Araç Sil")
            .setMessage("${car.carName} (${car.plate}) aracını silmek istediğinizden emin misiniz? Bu işlem geri alınamaz.")
            .setIcon(R.drawable.ic_delete)
            .setPositiveButton("Sil") { _, _ ->
                viewModel.deleteCar(car)
            }
            .setNegativeButton("İptal", null)
            .show()
    }

    private fun observeViewModel() {
        viewModel.loading.observe(viewLifecycleOwner) { isLoading ->
            binding.loadingAnimation.visibility = if (isLoading) View.VISIBLE else View.GONE

            binding.contentLayout.apply {
                alpha = if (isLoading) 0.5f else 1.0f
                isEnabled = !isLoading
            }
        }

        viewModel.deleteSuccess.observe(viewLifecycleOwner) { success ->
            if (success) {
                Toast.makeText(context, "Araç başarıyla silindi", Toast.LENGTH_SHORT).show()
                findNavController().navigateUp()
            }
        }

        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}