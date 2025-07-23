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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFindParkingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupButtons()
        observeViewModel() // Observer'lar butonları otomatik güncelleyecek
        viewModel.initialize()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // ✅ Adapter'ı null yapma - navigation'da state korunsun
        // parkAdapter = null  <-- KALDIRDIK
        _binding = null
    }

    // ✅ GERİ GELİNDİĞİNDE RESTORE
    override fun onResume() {
        super.onResume()
        println("DEBUG: onResume - Restoring display")

        // ✅ Adapter null olduysa yeniden bağla
        if (binding.rvParkingResults.adapter == null && parkAdapter != null) {
            println("DEBUG: Re-attaching adapter to RecyclerView")
            binding.rvParkingResults.adapter = parkAdapter
        }

        viewModel.restoreDisplay()
    }

    private fun setupRecyclerView() {
        binding.rvParkingResults.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun setupButtons() {
        binding.btnAll.setOnClickListener {
            viewModel.showAllParks()
            // updateButtonStates artık gerekmiyor - observer otomatik yapar
        }

        binding.btnFavorites.setOnClickListener {
            viewModel.showFavoritesOnly()
            // updateButtonStates artık gerekmiyor - observer otomatik yapar
        }
    }

    private fun updateButtonStates(showingFavorites: Boolean) {
        val selectedColor = ContextCompat.getColorStateList(requireContext(), R.color.colorPrimary)
        val unselectedColor = ContextCompat.getColorStateList(requireContext(), R.color.colorCardBackground)
        val selectedText = ContextCompat.getColor(requireContext(), R.color.textOnPrimary)
        val unselectedText = ContextCompat.getColor(requireContext(), R.color.textSecondary)

        if (showingFavorites) {
            binding.btnFavorites.backgroundTintList = selectedColor
            binding.btnFavorites.setTextColor(selectedText)
            binding.btnAll.backgroundTintList = unselectedColor
            binding.btnAll.setTextColor(unselectedText)
        } else {
            binding.btnAll.backgroundTintList = selectedColor
            binding.btnAll.setTextColor(selectedText)
            binding.btnFavorites.backgroundTintList = unselectedColor
            binding.btnFavorites.setTextColor(unselectedText)
        }
    }

    private fun observeViewModel() {
        // ✅ LISTE GÜNCELLEMELERİ
        viewModel.parkList.observe(viewLifecycleOwner) { parkList ->
            println("DEBUG: parkList observer - Size: ${parkList.size}")

            if (parkList.isEmpty()) {
                println("DEBUG: Park list is EMPTY!")
                binding.tvResultsCount.text = "0 Otopark Bulundu"
                return@observe
            }

            binding.tvResultsCount.text = "${parkList.size} Otopark Bulundu"

            if (parkAdapter == null) {
                println("DEBUG: Creating NEW adapter")
                // İlk kere adapter oluştur
                parkAdapter = ParkAdapter(
                    parkList = parkList.toMutableList(),
                    onItemClick = { park ->
                        findNavController().navigate(
                            FindParkingFragmentDirections.actionNavFindParkingToParkDetailFragment()
                        )
                    },
                    onFavoriteClick = { park, position ->
                        viewModel.toggleFavorite(park, position)
                    }
                )
                binding.rvParkingResults.adapter = parkAdapter
            } else {
                println("DEBUG: Updating existing adapter")
                // Mevcut adapter'ı güncelle
                parkAdapter?.updateList(parkList)
            }
        }

        // ✅ BUTON DURUMLARI - OTOMATIK GÜNCELLEME
        viewModel.showOnlyFavorites.observe(viewLifecycleOwner) { showingFavorites ->
            println("DEBUG: Button state update - Showing favorites: $showingFavorites")
            updateButtonStates(showingFavorites)
        }

        // ✅ TEK ITEM GÜNCELLEMESİ
        viewModel.favoriteUpdate.observe(viewLifecycleOwner) { (position, newState) ->
            println("DEBUG: favoriteUpdate - Position: $position, State: $newState")
            parkAdapter?.updateFavorite(position, newState)
        }

        // ✅ LOADING
        viewModel.loading.observe(viewLifecycleOwner) { isLoading ->
            println("DEBUG: Loading state: $isLoading")
            binding.loadingAnimation.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }
}