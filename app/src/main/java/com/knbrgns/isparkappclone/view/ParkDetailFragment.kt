package com.knbrgns.isparkappclone.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.knbrgns.isparkappclone.R
import com.knbrgns.isparkappclone.databinding.FragmentParkDetailBinding
import com.knbrgns.isparkappclone.model.Park
import com.knbrgns.isparkappclone.view.viewmodel.ParkDetailViewModel
import kotlin.getValue


class ParkDetailFragment : Fragment() {

    private var _binding: FragmentParkDetailBinding? = null
    private val binding get() = _binding!!

    private lateinit var park: Park
    private val args: ParkDetailFragmentArgs by navArgs()
    private val viewModel: ParkDetailViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentParkDetailBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        park = args.park
        setupParkInfo()
        observeViewModel()
        viewModel.getParkDetail(args.park.parkID)
    }

    private fun observeViewModel() {
        viewModel.parkDetail.observe(viewLifecycleOwner) { detailedPark ->
            park = detailedPark
            detailedPark?.let {
                setupParkInfo()
            }
        }

        viewModel.loading.observe(viewLifecycleOwner){ isLoading ->
            binding.loadingAnimation.visibility = if (isLoading) View.VISIBLE else View.GONE

            binding.detailParent.apply {
                alpha = if (isLoading) 0.5f else 1.0f
                isEnabled = !isLoading
            }

        }
    }

    private fun openMapsForDirections(lat: Double, lng: Double) {
        try {
            val uri = "google.navigation:q=$lat,$lng"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
            intent.setPackage("com.google.android.apps.maps")
            startActivity(intent)
        } catch (e: Exception) {
            val uri = "https://maps.google.com/maps?daddr=$lat,$lng"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
            startActivity(intent)
        }
    }

    private fun setupParkInfo() {
        with(binding) {
            tvParkName.text = park.parkName
            tvDistrict.text = park.district
            tvAddress.text = park.address
            tvEmptyCapacity.text = park.emptyCapacity.toString()
            tvWorkHours.text = park.workHours
            binding.btnFavorite.setIconResource(
                if (park.isFavorite) R.drawable.ic_heart_filled else R.drawable.ic_heart
            )
            if (park.freeTime != 0) {
                binding.tvFreeTime.text = HtmlCompat.fromHtml(
                    "İlk <b>${park.freeTime}</b> dk ücretsiz",
                    HtmlCompat.FROM_HTML_MODE_LEGACY
                )
            } else {
                binding.tvFreeTime.text = "Ücretsiz dakika avantajı yok"
            }
                tvtariff.text = park.tariff?.replace(";", "\n") ?: "Tarife Bulunamadı"
                tvParkType.text = park.parkType
            }
        }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}