package com.knbrgns.isparkappclone.view

import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
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
        setupSearchView()
        observeViewModel()
        viewModel.initialize()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        println("DEBUG: onResume - Restoring display")

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
        }

        binding.btnFavorites.setOnClickListener {
            viewModel.showFavoritesOnly()
        }
    }

    private fun updateButtonStates(showingFavorites: Boolean) {
        val selectedColor = ContextCompat.getColorStateList(requireContext(), R.color.colorPrimary)
        val unselectedColor =
            ContextCompat.getColorStateList(requireContext(), R.color.colorCardBackground)
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

    private fun setupSearchView() {
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val searchText = s.toString().trim()
                viewModel.searchParks(searchText)
            }
        })

        binding.etSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                hideKeyboard()
                true
            } else false
        }

        binding.ivSearchIcon.setOnClickListener {
            val query = binding.etSearch.text.toString().trim()
            viewModel.searchParks(query)
            hideKeyboard()
        }
    }

    private fun hideKeyboard() {
        val imm =
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.etSearch.windowToken, 0)
    }

    private fun observeViewModel() {
        viewModel.parkList.observe(viewLifecycleOwner) { parkList ->

            binding.tvResultsCount.text = "${parkList.size} Otopark Bulundu"

            if (parkAdapter == null) {
                parkAdapter = ParkAdapter(
                    parkList = parkList.toMutableList(),
                    onItemClick = { park ->
                        findNavController().navigate(
                            FindParkingFragmentDirections.actionNavFindParkingToParkDetailFragment(
                                park
                            )
                        )
                    },
                    onFavoriteClick = { park, position ->
                        viewModel.toggleFavorite(park, position)
                    }
                )
                binding.rvParkingResults.adapter = parkAdapter
            } else {
                parkAdapter?.updateList(parkList)
            }

        }

        viewModel.showOnlyFavorites.observe(viewLifecycleOwner) { showingFavorites ->
            updateButtonStates(showingFavorites)
        }

        viewModel.favoriteUpdate.observe(viewLifecycleOwner) { (position, newState) ->
            parkAdapter?.updateFavorite(position, newState)
        }

        viewModel.loading.observe(viewLifecycleOwner) { isLoading ->
            binding.loadingAnimation.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }
}