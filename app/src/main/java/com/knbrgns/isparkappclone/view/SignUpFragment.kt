package com.knbrgns.isparkappclone.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.knbrgns.isparkappclone.R
import com.knbrgns.isparkappclone.databinding.FragmentSignUpBinding
import com.knbrgns.isparkappclone.model.User
import com.knbrgns.isparkappclone.repository.AuthResult
import com.knbrgns.isparkappclone.view.viewmodel.SignUpViewModel
import kotlinx.coroutines.launch

class SignUpFragment : Fragment() {

    private var _binding: FragmentSignUpBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: SignUpViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignUpBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[SignUpViewModel::class.java]
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupClickListeners()
        observeAuthResult()
    }

    private fun setupClickListeners() {
        binding.btnSignup.setOnClickListener {
            handleSignUp()
        }
        binding.signInLayout.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun handleSignUp() {
        // XML'de etEmail olduğunu varsayıyoruz.
        val fullName = binding.etFullName.text.toString()
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()
        val confirmPassword = binding.etConfirmPassword.text.toString().trim()

        if (validateInputs(fullName,email, password, confirmPassword)) {
            viewModel.signUpUser(fullName,email, password)
        }
    }

    private fun validateInputs(fullName: String, email: String, password: String, confirmPassword: String): Boolean {
        clearErrors()
        var isValid = true
        if(fullName.isEmpty()){
            binding.tilFullName.error = "Adınızı girin"
            isValid = false
        }
        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.tilEmail.error = "Geçerli bir e-posta adresi girin"
            isValid = false
        }
        if (password.length < 6) {
            binding.tilPassword.error = "Şifre en az 6 karakter olmalıdır"
            isValid = false
        }
        if (password != confirmPassword) {
            binding.tilConfirmPassword.error = "Şifreler eşleşmiyor"
            isValid = false
        }
        return isValid
    }

    private fun clearErrors() {
        binding.tilEmail.error = null
        binding.tilPassword.error = null
        binding.tilConfirmPassword.error = null
    }

    private fun observeAuthResult() {
        lifecycleScope.launch {
            viewModel.authResult.collect { result ->
                setLoadingState(false)
                when (result) {
                    is AuthResult.Loading -> setLoadingState(true)
                    is AuthResult.Success -> {
                        saveUserToFirestore()
                        findNavController().navigate(R.id.action_signUpFragment_to_nav_home)
                    }
                    is AuthResult.Error -> {
                        Toast.makeText(context, "Hata: ${result.message}", Toast.LENGTH_LONG).show()
                    }
                    else -> {}
                }
            }
        }
    }

    private suspend fun saveUserToFirestore() {
        val currentUser = viewModel.getCurrentUser()
        val user = User(
            currentUser!!.uid,
            binding.etFullName.text.toString(),
            binding.etEmail.text.toString()
        )
        viewModel.saveUserToFirestore(user)
    }

    private fun setLoadingState(isLoading: Boolean) {
        binding.loadingAnimation.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.btnSignup.isEnabled = !isLoading
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}