package com.knbrgns.isparkappclone.view

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.knbrgns.isparkappclone.databinding.FragmentFindParkingBinding
import com.knbrgns.isparkappclone.model.Park
import com.knbrgns.isparkappclone.view.adapter.ParkAdapter
import com.knbrgns.isparkappclone.view.viewmodel.FindParkingViewModel

class FindParkingFragment : Fragment() {

    private var _binding: FragmentFindParkingBinding? = null
    private val binding get() = _binding!!
    private val viewModel: FindParkingViewModel by viewModels()
    private var allParks: List<Park> = emptyList()
    private var favoriteIds: List<Int> = emptyList()
    private var showOnlyFavorites = false

    private lateinit var parkAdapter: ParkAdapter


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFindParkingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupSearchBar()
        setupButtons()
        observeViewModel()
        viewModel.initialize()
    }

    private fun setupRecyclerView() {
        parkAdapter = ParkAdapter(
            onItemClick = { park -> onParkItemClick(park) },
            onFavoriteClick = { park ->
                viewModel.toggleFavorite(park)
            }
        )

        binding.rvParkingResults.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = parkAdapter
            setHasFixedSize(true)
        }
    }

    private fun setupButtons() {

        updateButtonStates(false)

        binding.btnNews.setOnClickListener {
            showOnlyFavorites = false
            updateButtonStates(false)
            updateList()
        }

        binding.btnCampaigns.setOnClickListener {
            showOnlyFavorites = true
            updateButtonStates(true)
            updateList()
        }
    }

    private fun updateButtonStates(favoritesSelected: Boolean) {
        binding.btnNews.isChecked = !favoritesSelected
        binding.btnCampaigns.isChecked = favoritesSelected
    }

    private fun updateList() {
        val searchText = binding.etSearch.text.toString().trim()

        val filteredByFavorites = if (showOnlyFavorites) {
            allParks.filter { favoriteIds.contains(it.parkID) }
        } else {
            allParks
        }

        val finalList = if (searchText.isEmpty()) {
            filteredByFavorites
        } else {
            filteredByFavorites.filter { park ->
                park.parkName.contains(searchText, ignoreCase = true) ||
                        park.district.contains(searchText, ignoreCase = true) ||
                        park.address?.contains(searchText, ignoreCase = true) == true
            }
        }

        // ✅ ADAPTER'I YENİDEN OLUŞTURMA, SADECE DATA'YI GÜNCELLE
        parkAdapter.updateData(finalList, favoriteIds)

        val resultText = if (showOnlyFavorites) {
            "${finalList.size} favori otopark"
        } else {
            "${finalList.size} otopark bulundu"
        }
        binding.tvResultsCount.text = resultText
    }

    private fun setupSearchBar() {
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            private var searchRunnable: Runnable? = null

            override fun afterTextChanged(s: Editable?) {
                searchRunnable?.let { handler.removeCallbacks(it) }
                searchRunnable = Runnable { updateList() }
                handler.postDelayed(searchRunnable!!, 300)
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        })
    }

    private fun observeViewModel() {
        viewModel.parkList.observe(viewLifecycleOwner) { parks ->
            allParks = parks
            updateList()
        }

        viewModel.favoriteIds.observe(viewLifecycleOwner) { favorites ->
            favoriteIds = favorites
            updateList()
        }

        viewModel.loading.observe(viewLifecycleOwner) { loading ->
            binding.loadingAnimation.visibility = if (loading) View.VISIBLE else View.GONE
        }
    }

    private fun onParkItemClick(park: Park) {
        val action = FindParkingFragmentDirections.actionNavFindParkingToParkDetailFragment()
        findNavController().navigate(action)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private val handler = Handler(Looper.getMainLooper())
    }
}