package com.knbrgns.isparkappclone.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.knbrgns.isparkappclone.R
import com.knbrgns.isparkappclone.databinding.FragmentProfileBinding
import com.knbrgns.isparkappclone.repository.FirebaseRepo
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
        viewModel.getUser()
    }

    fun setupObserver() {
        viewModel.users.observe(viewLifecycleOwner) { user ->
            binding.tvUserName.text = user.name
        }
    }

    fun navigate() {

    }

    fun logout() {

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}