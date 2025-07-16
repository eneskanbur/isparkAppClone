package com.knbrgns.isparkappclone

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import androidx.activity.OnBackPressedCallback
import androidx.navigation.ui.navigateUp
import com.knbrgns.isparkappclone.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        setupNavigation()
        setupDrawer()
        setupBackPressedCallback()
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

        binding.ivHamburger.setOnClickListener {
            if (binding.root.isDrawerOpen(binding.navView)) {
                binding.root.closeDrawer(binding.navView)
            } else {
                binding.root.openDrawer(binding.navView)
            }
        }
    }

    private fun setupBackPressedCallback() {
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (binding.root.isDrawerOpen(binding.navView)) {
                    binding.root.closeDrawer(binding.navView)
                } else {
                    if (navController.currentDestination?.id == R.id.nav_home) {
                        finish()
                    } else {
                        navController.navigateUp()
                    }
                }
            }
        }
        onBackPressedDispatcher.addCallback(this, callback)
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}