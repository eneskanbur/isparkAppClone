package com.knbrgns.isparkappclone.view

import android.app.ActivityOptions
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.knbrgns.isparkappclone.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private lateinit var prefs: SharedPreferences
    private var isInitializing = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        prefs = requireContext().getSharedPreferences("ispark_settings", Context.MODE_PRIVATE)

        isInitializing = true
        setupThemeSettings()
        setupLanguageSettings()
        isInitializing = false
    }

    private fun setupThemeSettings() {
        val isDarkMode = prefs.getBoolean("dark_mode", false)
        binding.switchTheme.isChecked = isDarkMode

        binding.switchTheme.setOnCheckedChangeListener { _, isChecked ->
            if (isInitializing) return@setOnCheckedChangeListener

            prefs.edit().putBoolean("dark_mode", isChecked).apply()

            AppCompatDelegate.setDefaultNightMode(
                if (isChecked) AppCompatDelegate.MODE_NIGHT_YES
                else AppCompatDelegate.MODE_NIGHT_NO
            )

            restartActivitySafely()

        }
    }

    private fun restartActivitySafely() {
        val activity = this.activity ?: return
        val intent = activity.intent

        Handler(Looper.getMainLooper()).postDelayed({
            if (!activity.isFinishing && !activity.isDestroyed) {
                val options = ActivityOptions.makeCustomAnimation(activity, 0, 0)
                activity.startActivity(intent, options.toBundle())
                activity.finish()
            }
        }, 100)
    }


    private fun setupLanguageSettings() {
        val languages = arrayOf("Sistem Varsayılanı", "Türkçe", "English")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, languages)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerLanguage.adapter = adapter

        val currentLang = prefs.getString("language", "system") ?: "system"
        val position = when (currentLang) {
            "system" -> 0
            "tr" -> 1
            "en" -> 2
            else -> 0
        }
        binding.spinnerLanguage.setSelection(position, false)

        binding.spinnerLanguage.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (isInitializing) return

                val newLanguage = when (position) {
                    0 -> "system"
                    1 -> "tr"
                    2 -> "en"
                    else -> "system"
                }

                if (newLanguage != currentLang) {
                    prefs.edit().putString("language", newLanguage).apply()
                    restartActivitySafely()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}