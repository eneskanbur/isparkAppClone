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
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.knbrgns.isparkappclone.R
import com.knbrgns.isparkappclone.databinding.FragmentFindParkingBinding
import com.knbrgns.isparkappclone.model.Park
import com.knbrgns.isparkappclone.view.adapter.ParkAdapter
import com.knbrgns.isparkappclone.view.viewmodel.FindParkingViewModel

class FindParkingFragment : Fragment() {

    private var _binding: FragmentFindParkingBinding? = null
    private val binding get() = _binding!!
    private val viewModel: FindParkingViewModel by viewModels()
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
        observeViewModel()
        viewModel.initialize()
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
            updateButtonStates(showingFavorites = false)
            viewModel.showAllParks()
        }

        binding.btnFavorites.setOnClickListener {
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
        viewModel.parkList.observe(viewLifecycleOwner) { parkList ->
            println("DEBUG: Observer triggered, park count: ${parkList.size}")

            parkAdapter = ParkAdapter(
                parkList = parkList,
                onItemClick = { park -> onParkItemClick(park) },
                onFavoriteClick = { park -> viewModel.toggleFavorite(park) }
            )
            binding.rvParkingResults.adapter = parkAdapter

            binding.tvResultsCount.text = "${parkList.size} Otopark Bulundu"
        }

        viewModel.loading.observe(viewLifecycleOwner) { isLoading ->
            println("DEBUG: Loading state: $isLoading")
            binding.loadingAnimation.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.rvParkingResults.visibility = if (isLoading) View.GONE else View.VISIBLE
        }
    }

    private fun onParkItemClick(park: Park) {
        val action = FindParkingFragmentDirections.actionNavFindParkingToParkDetailFragment()
        findNavController().navigate(action)
    }
}