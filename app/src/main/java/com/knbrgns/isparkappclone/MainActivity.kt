package com.knbrgns.isparkappclone

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.knbrgns.isparkappclone.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, 0, systemBars.right, systemBars.bottom)
            insets
        }
        setupNavigation()
        setupDrawer()
    }

    private fun setupNavigation() {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.navHostFragment) as NavHostFragment
        navController = navHostFragment.navController

        binding.bottomNavigation.setupWithNavController(navController)
    }

    private fun setupDrawer() {
        // Drawer için AppBarConfiguration
        appBarConfiguration = AppBarConfiguration(
            setOf(R.id.nav_home, R.id.nav_parking, R.id.nav_campaigns, R.id.nav_profile),
            binding.root
        )

        // Drawer Navigation ile NavController'ı bağla
        binding.navView.setupWithNavController(navController)

        // Hamburger menü animasyonu
        binding.lottieHamburger.setOnClickListener {
            if (binding.root.isDrawerOpen(binding.navView)) {
                binding.root.closeDrawer(binding.navView)
                binding.lottieHamburger.setMinAndMaxFrame(10, 20)
            } else {
                binding.root.openDrawer(binding.navView)
                binding.lottieHamburger.setMinAndMaxFrame(0, 10)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }

}