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
    private var parkAdapter: ParkAdapter? = null

    private var allParks: List<Park> = emptyList()
    private var filteredParks: List<Park> = emptyList()


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
        setupSearchBar()
        observeViewModel()
        viewModel.loading.value = false
        viewModel.initialize()
    }

    private fun setupSearchBar() {
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            private var searchRunnable: Runnable? = null

            override fun afterTextChanged(s: Editable?) {

                searchRunnable?.let { handler.removeCallbacks(it) }

                searchRunnable = Runnable {
                    val searchText = s.toString().trim()
                    filterParks(searchText)
                }

                handler.postDelayed(searchRunnable!!, 300)
            }

            override fun beforeTextChanged(
                p0: CharSequence?,
                p1: Int,
                p2: Int,
                p3: Int
            ) {

            }

            override fun onTextChanged(
                p0: CharSequence?,
                p1: Int,
                p2: Int,
                p3: Int
            ) {
            }
        })
    }

    companion object {
        private val handler = Handler(Looper.getMainLooper())
    }


    fun setupRecyclerView() {
        binding.rvParkingResults.apply {
            layoutManager = LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.VERTICAL,
                false
            )
            setHasFixedSize(true)
        }
    }

    fun observeViewModel() {
        viewModel.parkList.observe(viewLifecycleOwner) { parkList ->
            parkList.let {
                parkAdapter = ParkAdapter(it) { selectedPark ->
                    onParkItemClick(selectedPark)
                }
                binding.rvParkingResults.adapter = parkAdapter

            }
        }

        viewModel.loading.observe(viewLifecycleOwner) { isLoading ->
            binding.loadingAnimation.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.parkList.observe(viewLifecycleOwner) { parkList ->
            allParks = parkList
            filteredParks = parkList
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


}