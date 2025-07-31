package com.knbrgns.isparkappclone.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.knbrgns.isparkappclone.databinding.FragmentSignUpBinding
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

        binding.btnSignup.setOnClickListener {
            val phoneNumber = binding.etPhone.text.toString().trim()
            val fullName = binding.etFullName.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            val confirmPassword = binding.etConfirmPassword.text.toString().trim()

            if (phoneNumber.length != 10) {
                binding.tilPhone.error = "Geçerli bir telefon numarası girin (10 haneli)."
                return@setOnClickListener
            }
            if (password != confirmPassword) {
                binding.tilConfirmPassword.error = "Şifreler eşleşmiyor."
                return@setOnClickListener
            }
            binding.tilPhone.error = null
            binding.tilConfirmPassword.error = null

            val fullPhoneNumber = "+90$phoneNumber"
            activity?.let {
                viewModel.startPhoneNumberVerification(fullPhoneNumber, it)
            }
        }

        observeAuthResult()
    }

    private fun observeAuthResult() {
        lifecycleScope.launch {
            viewModel.authResult.collect { result ->
                when (result) {
                    is AuthResult.Loading -> {
                        binding.loadingAnimation.visibility = View.VISIBLE
                    }
                    is AuthResult.CodeSent -> {
                        binding.loadingAnimation.visibility = View.GONE
                        Toast.makeText(context, "Doğrulama kodu gönderildi.", Toast.LENGTH_SHORT).show()
                        // Burada kullanıcıyı SMS kodunu gireceği ekrana yönlendirebilirsiniz.
                    }
                    is AuthResult.Success -> {
                        binding.loadingAnimation.visibility = View.GONE
                        Toast.makeText(context, "Kayıt başarılı!", Toast.LENGTH_LONG).show()
                        // Ana ekrana veya profil ekranına yönlendirme yapabilirsiniz.
                    }
                    is AuthResult.Error -> {
                        binding.loadingAnimation.visibility = View.GONE
                        Toast.makeText(context, "Hata: ${result.message}", Toast.LENGTH_LONG).show()
                    }
                    is AuthResult.Idle -> {
                        binding.loadingAnimation.visibility = View.GONE
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}