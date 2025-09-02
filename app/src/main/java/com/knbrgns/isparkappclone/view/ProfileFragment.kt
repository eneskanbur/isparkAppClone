package com.knbrgns.isparkappclone.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.knbrgns.isparkappclone.R
import com.knbrgns.isparkappclone.databinding.FragmentProfileBinding
import com.knbrgns.isparkappclone.repository.FirebaseRepo
import com.knbrgns.isparkappclone.view.dialog.ErrorDialog
import com.knbrgns.isparkappclone.view.viewmodel.ProfileViewModel
import kotlinx.coroutines.launch

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val viewModel: ProfileViewModel by viewModels()
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObserver()
        setupButtons()
        viewModel.getUser()
    }

    private fun setupButtons() {
        binding.btnLogout.setOnClickListener {
            logout()
        }

        binding.layoutMyCar.setOnClickListener {
            navigate(R.id.action_nav_profile_to_myCarFragment)
        }

        binding.layoutSettings.setOnClickListener {
            navigate(R.id.action_nav_profile_to_settingsFragment)
        }

        binding.layoutParkingHistory.setOnClickListener {
            ErrorDialog.Builder(requireContext())
                .setCancelable(true)
                .setOnConfirm {  }
                .setErrorTitle("İşlem Başarısız")
                .setErrorMessage("Bu işlem henüz uygulanmamaktadır.")
                .build()
                .show()
        }

        binding.layoutPaymentMethods.setOnClickListener {
            ErrorDialog.Builder(requireContext())
                .setCancelable(true)
                .setOnConfirm {  }
                .setErrorTitle("İşlem Başarısız")
                .setErrorMessage("Bu işlem henüz uygulanmamaktadır.")
                .build()
                .show()
        }
    }

    fun setupObserver() {
        viewModel.users.observe(viewLifecycleOwner) { user ->
            binding.tvUserName.text = user.name
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.btnLogout.isEnabled = !isLoading
            binding.btnLogout.text = if (isLoading) "Çıkış yapılıyor..." else "Çıkış Yap"
        }

        viewModel.logoutSuccess.observe(viewLifecycleOwner) { success ->
            if (success) {
                navigate(R.id.action_nav_profile_to_signInFragment)
            }
        }
    }

    fun navigate(id: Int) {
        findNavController().navigate(id)
    }

    private fun logout() {
        viewModel.logout()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
