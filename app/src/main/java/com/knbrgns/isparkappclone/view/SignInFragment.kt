package com.knbrgns.isparkappclone.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.knbrgns.isparkappclone.R
import com.knbrgns.isparkappclone.databinding.FragmentSignInBinding
import com.knbrgns.isparkappclone.repository.AuthResult
import com.knbrgns.isparkappclone.view.viewmodel.SignInViewModel
import kotlinx.coroutines.launch

class SignInFragment : Fragment() {

    private var _binding: FragmentSignInBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: SignInViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignInBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[SignInViewModel::class.java]
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        setupClickListeners()
        observeAuthResult()
        setupBackPress()
    }

    private fun setupBackPress() {
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                requireActivity().finish()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }

    private fun setupUI() {
        binding.tilPassword.visibility = View.VISIBLE
        binding.tvForgotPassword.visibility = View.VISIBLE

    }

    private fun setupClickListeners() {
        binding.btnLogin.setOnClickListener {
            handleLogin()
        }
        binding.signUpLayout.setOnClickListener {
            findNavController().navigate(R.id.action_signInFragment_to_signUpFragment)
        }
    }

    private fun handleLogin() {
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()

        if (validateInputs(email, password)) {
            viewModel.signInUser(email, password)
        }
    }

    private fun validateInputs(email: String, password: String): Boolean {
        clearErrors()
        var isValid = true
        if (email.isEmpty()) {
            binding.tilEmail.error = "E-posta boş bırakılamaz"
            isValid = false
        }
        if (password.isEmpty()) {
            binding.tilPassword.error = "Şifre boş bırakılamaz"
            isValid = false
        }
        return isValid
    }

    private fun clearErrors() {
        binding.tilEmail.error = null
        binding.tilPassword.error = null
    }

    private fun observeAuthResult() {
        lifecycleScope.launch {
            viewModel.authResult.collect { result ->
                setLoadingState(false)
                when (result) {
                    is AuthResult.Loading -> setLoadingState(true)
                    is AuthResult.Success -> {
                        Toast.makeText(context, "Giriş başarılı!", Toast.LENGTH_SHORT).show()
                        findNavController().navigate(R.id.action_signInFragment_to_nav_home)
                    }

                    is AuthResult.Error -> {
                        Toast.makeText(context, "Hata: ${result.message}", Toast.LENGTH_LONG).show()
                    }

                    else -> {}
                }
            }
        }
    }

    private fun setLoadingState(isLoading: Boolean) {
        binding.btnLogin.isEnabled = !isLoading
        binding.btnLogin.text = if (isLoading) "Giriş Yapılıyor..." else "Giriş Yap"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}