package com.knbrgns.isparkappclone.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.Toast
import androidx.core.text.HtmlCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
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
                setupMapView()
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

    private fun showDirectionsToParking(lat: Double, lng: Double) {
        try {
            val uri = "https://www.google.com/maps/dir/?api=1&destination=$lat,$lng&travelmode=driving"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
            startActivity(intent)
        } catch (e: Exception) {
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

    private fun setupMapView() {
        val latStr = park.lat
        val lngStr = park.lng

        val lat = latStr.toDoubleOrNull() ?: 0.0
        val lng = lngStr.toDoubleOrNull() ?: 0.0

        if (lat == 0.0 || lng == 0.0) {
            binding.mapPlaceholder.setImageResource(R.drawable.ic_location)
            return
        }

        val osmMapUrl = "https://www.openstreetmap.org/export/embed.html?" +
                "bbox=${lng-0.005},${lat-0.005},${lng+0.005},${lat+0.005}" +
                "&layer=mapnik" +
                "&marker=$lat,$lng"

        binding.mapPlaceholder.visibility = View.GONE

        val webView = WebView(requireContext()).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            settings.javaScriptEnabled = true
            loadUrl(osmMapUrl)
        }

        (binding.mapPlaceholder.parent as ViewGroup).addView(webView)

        binding.fabDirections.setOnClickListener {
            showDirectionsToParking(lat, lng)
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}