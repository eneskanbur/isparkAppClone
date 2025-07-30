package com.knbrgns.isparkappclone.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.knbrgns.isparkappclone.R
import com.knbrgns.isparkappclone.databinding.FragmentMyCarBinding
import com.knbrgns.isparkappclone.model.Car
import com.knbrgns.isparkappclone.view.adapter.CarAdapter
import com.knbrgns.isparkappclone.view.viewmodel.MyCarViewModel

class MyCarFragment : Fragment() {
    private var _binding: FragmentMyCarBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MyCarViewModel by viewModels()
    private var carAdapter: CarAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMyCarBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        observeViewModel()
        setupButtons()
        viewModel.initialize()
    }

    private fun setupButtons() {
        binding.fabAddCar.setOnClickListener {
            showAddCarDialog()
        }
    }

    private fun showAddCarDialog() {
        val dialog = AddCarDialogFragment { newCar ->
            viewModel.addCar(newCar)
        }

        dialog.show(childFragmentManager, "AddCarDialog")
    }

    private fun observeViewModel() {
        viewModel.cars.observe(viewLifecycleOwner) { cars ->
            updateUI(cars)
        }

        viewModel.loading.observe(viewLifecycleOwner) { isLoading ->
            binding.loadingAnimation.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }

    private fun updateUI(cars: List<Car>) {
        if (cars.isEmpty()) {
            binding.rvCarList.visibility = View.GONE
            binding.emptyStateLayout.visibility = View.VISIBLE
        } else {
            binding.rvCarList.visibility = View.VISIBLE
            binding.emptyStateLayout.visibility = View.GONE

            carAdapter = CarAdapter(cars) { selectedCar ->
                onCarItemClick(selectedCar)
            }
            binding.rvCarList.adapter = carAdapter
        }
    }

    private fun onCarItemClick(car: Car) {
        val action = MyCarFragmentDirections.actionMyCarFragmentToCarDetailFragment(car)
        findNavController().navigate(action)
    }

    private fun setupRecyclerView() {
        binding.rvCarList.apply {
            layoutManager = LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.VERTICAL,
                false
            )
            setHasFixedSize(true)
            setItemViewCacheSize(20)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}