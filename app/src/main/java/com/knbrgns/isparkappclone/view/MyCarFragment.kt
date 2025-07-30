package com.knbrgns.isparkappclone.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
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
        val view = binding.root
        return view

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

        }
    }

    private fun observeViewModel() {
        viewModel.cars.observe(viewLifecycleOwner) { cars ->
            cars?.let {
                carAdapter = CarAdapter(cars){ selectedCar ->
                    onCarItemClick(selectedCar)
                }
                binding.rvCarList.adapter = carAdapter
            }
        }
    }

    private fun onCarItemClick(car: Car) {

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