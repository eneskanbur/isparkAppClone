package com.knbrgns.isparkappclone.view

import android.os.Bundle
import android.os.Parcelable
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.knbrgns.isparkappclone.R
import com.knbrgns.isparkappclone.databinding.FragmentFindParkingBinding
import com.knbrgns.isparkappclone.model.Park
import com.knbrgns.isparkappclone.view.adapter.ParkAdapter
import com.knbrgns.isparkappclone.view.viewmodel.FindParkingViewModel

class FindParkingFragment : Fragment() {

    private var _binding: FragmentFindParkingBinding? = null
    private val binding get() = _binding!!

    private val viewModel: FindParkingViewModel by navGraphViewModels(R.id.nav_graph)

    private var parkAdapter: ParkAdapter? = null
    private var recyclerViewState: Parcelable? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFindParkingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        println("DEBUG: FindParkingFragment - onViewCreated")

        setupRecyclerView()
        setupButtons()
        observeViewModel()

        // ✅ STATE MANAGEMENT: ViewModel initialize edilirse data zaten var
        viewModel.initialize()
    }

    // ✅ SCROLL STATE SAVE: Fragment destroy edilmeden önce state kaydet
    override fun onDestroyView() {
        super.onDestroyView()

        // RecyclerView scroll state'ini kaydet
        recyclerViewState = binding.rvParkingResults.layoutManager?.onSaveInstanceState()
        println("DEBUG: ScrollView state saved on destroy")

        // Adapter'ı null yapma - ViewModel state korunuyor
        parkAdapter = null
        _binding = null
    }

    // ✅ onResume basitleştirildi - state korunuyor
    override fun onResume() {
        super.onResume()
        println("DEBUG: FindParkingFragment - onResume")

        // Navigation-safe ViewModel sayesinde data zaten korunuyor
        // Gereksiz restore işlemi yok
    }

    private fun setupRecyclerView() {
        binding.rvParkingResults.apply {
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
        }
    }

    private fun setupButtons() {
        updateButtonStates(showingFavorites = false)

        binding.btnAll.setOnClickListener {
            println("DEBUG: All button clicked")
            updateButtonStates(showingFavorites = false)
            viewModel.showAllParks()
        }

        binding.btnFavorites.setOnClickListener {
            println("DEBUG: Favorites button clicked")
            updateButtonStates(showingFavorites = true)
            viewModel.showFavoritesOnly()
        }
    }

    private fun updateButtonStates(showingFavorites: Boolean) {
        if (showingFavorites) {
            binding.btnFavorites.apply {
                backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.colorPrimary)
                setTextColor(ContextCompat.getColor(requireContext(), R.color.textOnPrimary))
            }
            binding.btnAll.apply {
                backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.colorCardBackground)
                setTextColor(ContextCompat.getColor(requireContext(), R.color.textSecondary))
            }
        } else {
            binding.btnAll.apply {
                backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.colorPrimary)
                setTextColor(ContextCompat.getColor(requireContext(), R.color.textOnPrimary))
            }
            binding.btnFavorites.apply {
                backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.colorCardBackground)
                setTextColor(ContextCompat.getColor(requireContext(), R.color.textSecondary))
            }
        }
    }

    private fun observeViewModel() {
        // ✅ Liste güncellemeleri
        viewModel.parkList.observe(viewLifecycleOwner) { parkList ->
            println("DEBUG: ParkList observer triggered - Count: ${parkList.size}")

            if (parkList.isEmpty()) {
                println("DEBUG: WARNING - Park list is empty!")
                binding.rvParkingResults.visibility = View.GONE
                binding.tvResultsCount.text = "0 Otopark Bulundu"
                return@observe
            }

            binding.rvParkingResults.visibility = View.VISIBLE

            // ✅ BASIT ÇÖZÜM: Her seferinde yeni adapter oluştur
            println("DEBUG: Creating fresh adapter with ${parkList.size} items")
            parkAdapter = ParkAdapter(
                parkList = parkList.toMutableList(),
                onItemClick = { park -> onParkItemClick(park) },
                onFavoriteClick = { park, position ->
                    println("DEBUG: Heart clicked - Park: ${park.parkID}, Position: $position")
                    viewModel.toggleFavorite(park, position)
                }
            )
            binding.rvParkingResults.adapter = parkAdapter
            println("DEBUG: Fresh adapter set to RecyclerView - ItemCount: ${parkAdapter?.itemCount}")

            binding.tvResultsCount.text = "${parkList.size} Otopark Bulundu"
        }

        // ✅ Tek item güncelleme - Artık gerekmiyor (her seferinde yeni adapter)


        // ✅ Loading durumu
        viewModel.loading.observe(viewLifecycleOwner) { isLoading ->
            println("DEBUG: Loading state: $isLoading")
            binding.loadingAnimation.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }

    private fun onParkItemClick(park: Park) {
        println("DEBUG: Park item clicked - ID: ${park.parkID}, Name: ${park.parkName}")

        // ✅ NAVIGATION: Park objesini argument olarak geç
        val action = FindParkingFragmentDirections
            .actionNavFindParkingToParkDetailFragment()
        findNavController().navigate(action)
    }

    override fun onPause() {
        super.onPause()
        println("DEBUG: FindParkingFragment - onPause")
    }

}