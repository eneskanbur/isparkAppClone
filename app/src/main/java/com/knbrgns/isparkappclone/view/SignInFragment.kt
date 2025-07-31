package com.knbrgns.isparkappclone.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
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

        binding.tilPhoneNumber.hint = "Telefon Numarası"
        binding.etEmail.inputType = android.text.InputType.TYPE_CLASS_PHONE

        // Şifre alanı bu senaryoda kullanılmayacağı için gizleyebiliriz.
        binding.tilPassword.visibility = View.GONE
        binding.tvForgotPassword.visibility = View.GONE


        binding.btnLogin.setOnClickListener {
            val phoneNumber = binding.etEmail.text.toString().trim()

            if (phoneNumber.length != 10) {
                binding.tilPhoneNumber.error = "Lütfen 10 haneli telefon numaranızı girin (örn: 5xxxxxxxxx)."
                return@setOnClickListener
            }
            binding.tilPhoneNumber.error = null

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
                setLoadingState(false)
                when (result) {
                    is AuthResult.Loading -> setLoadingState(true)
                    is AuthResult.CodeSent -> {
                        Toast.makeText(context, "Doğrulama kodu telefonunuza gönderildi.", Toast.LENGTH_SHORT).show()
                        // Kullanıcıyı SMS kodunu gireceği bir sonraki ekrana/dialog'a yönlendir.
                    }
                    is AuthResult.Success -> {
                        Toast.makeText(context, "Giriş başarılı!", Toast.LENGTH_LONG).show()
                        // Uygulamanın ana ekranına yönlendirme yap.
                    }
                    is AuthResult.Error -> {
                        Toast.makeText(context, "Hata: ${result.message}", Toast.LENGTH_LONG).show()
                    }
                    is AuthResult.Idle -> {

                    }
                }
            }
        }
    }

    private fun setLoadingState(isLoading: Boolean) {
        // binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.btnLogin.isEnabled = !isLoading
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}