package com.knbrgns.isparkappclone

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import androidx.activity.OnBackPressedCallback
import androidx.navigation.ui.navigateUp
import androidx.core.net.toUri
import androidx.navigation.NavOptions
import androidx.navigation.ui.NavigationUI
import com.google.firebase.auth.FirebaseAuth
import com.knbrgns.isparkappclone.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)


        auth = FirebaseAuth.getInstance()

        setupNavigation()
        setupDrawer()
        setupSocialMediaDrawer()
        setupBackPressedCallback()
        setupDestinationListener()
        checkCurrentUser()

    }

    private fun checkCurrentUser() {
        if (auth.currentUser != null && navController.currentDestination?.id == R.id.signInFragment) {
            val navOptions = NavOptions.Builder()
                .setPopUpTo(R.id.signInFragment, true)
                .build()
            navController.navigate(R.id.action_signInFragment_to_nav_home, null, navOptions)
        }
    }

    private fun setupNavigation() {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.navHostFragment) as NavHostFragment
        navController = navHostFragment.navController

        binding.bottomNavigation.setupWithNavController(navController)

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            navController.popBackStack()
            navController.navigate(item.itemId)
            true
        }
    }

    private fun setupDestinationListener() {
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.signInFragment, R.id.signUpFragment -> {
                    binding.bottomNavigation.visibility = View.GONE
                    binding.appBarLayout.visibility = View.GONE
                }

                else -> {
                    binding.bottomNavigation.visibility = View.VISIBLE
                    binding.appBarLayout.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun setupDrawer() {
        appBarConfiguration = AppBarConfiguration(
            setOf(R.id.nav_home, R.id.nav_parking, R.id.nav_campaigns, R.id.nav_profile),
            binding.root
        )

        binding.navView.setupWithNavController(navController)

        binding.navView.setNavigationItemSelectedListener { menuItem ->
            var navigationSuccess = false

            when (menuItem.itemId) {
                R.id.nav_logout -> {
                    auth.signOut()
                    binding.root.closeDrawers()

                    val navOptions = NavOptions.Builder()
                        .setPopUpTo(R.id.nav_graph, true)
                        .build()

                    try {
                        navController.navigate(R.id.signInFragment, null, navOptions)
                        navigationSuccess = true
                    } catch (e: Exception) {
                        // Navigation başarısız
                        navigationSuccess = false
                    }
                }

                R.id.nav_home -> {
                    try {
                        if (navController.currentDestination?.id != R.id.nav_home) {
                            navController.navigate(R.id.nav_home)
                        }
                        navigationSuccess = true
                    } catch (e: Exception) {
                        navigationSuccess = false
                    }
                }

                R.id.nav_parking -> {
                    try {
                        if (navController.currentDestination?.id != R.id.nav_find_parking) {
                            navController.navigate(R.id.nav_find_parking)
                        }
                        navigationSuccess = true
                    } catch (e: Exception) {
                        navigationSuccess = false
                    }
                }

                R.id.nav_news -> {
                    // Haberler -> HomePage
                    try {
                        if (navController.currentDestination?.id != R.id.nav_home) {
                            navController.navigate(R.id.nav_home)
                        }
                        navigationSuccess = true
                    } catch (e: Exception) {
                        navigationSuccess = false
                    }
                }

                R.id.nav_campaigns -> {
                    try {
                        if (navController.currentDestination?.id != R.id.nav_home) {
                            navController.navigate(R.id.nav_home)
                        }
                        navigationSuccess = true
                    } catch (e: Exception) {
                        navigationSuccess = false
                    }
                }

                R.id.nav_profile -> {
                    try {
                        if (navController.currentDestination?.id != R.id.nav_profile) {
                            navController.navigate(R.id.nav_profile)
                        }
                        navigationSuccess = true
                    } catch (e: Exception) {
                        navigationSuccess = false
                    }
                }

                // Diğer drawer itemleri için genel handling
                else -> {
                    try {
                        navigationSuccess =
                            NavigationUI.onNavDestinationSelected(menuItem, navController)
                    } catch (e: Exception) {
                        navigationSuccess = false
                    }
                }
            }

            // Sadece navigation başarılı olursa drawer'ı kapat
            if (navigationSuccess) {
                binding.root.closeDrawers()
            }

            navigationSuccess
        }

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
                    if (navController.currentDestination?.id == R.id.signInFragment) {
                        finish()
                        return
                    }

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

    private fun setupSocialMediaDrawer() {
        binding.navView.getHeaderView(0)?.let { headerView ->
            headerView.findViewById<ImageView>(R.id.ivIstanbul34Drawer)?.setOnClickListener {
                openWebUrl("https://istanbulsenin.istanbul")
            }

            headerView.findViewById<ImageView>(R.id.ivFacebookDrawer)?.setOnClickListener {
                openWebUrl("https://www.facebook.com/isparkas/?locale=tr_TR")
            }

            headerView.findViewById<ImageView>(R.id.ivXDrawer)?.setOnClickListener {
                openWebUrl("https://x.com/ispark?ref_src=twsrc%5Egoogle%7Ctwcamp%5Eserp%7Ctwgr%5Eauthor")
            }

            headerView.findViewById<ImageView>(R.id.ivInstagramDrawer)?.setOnClickListener {
                openWebUrl("https://instagram.com/ispark_as")
            }

            headerView.findViewById<ImageView>(R.id.ivYoutubeDrawer)?.setOnClickListener {
                openWebUrl("https://www.youtube.com/c/ispark")
            }

            headerView.findViewById<ImageView>(R.id.ivLinkedInDrawer)?.setOnClickListener {
                openWebUrl("https://www.linkedin.com/company/isparkas/posts/?feedView=all")
            }
        }
    }

    private fun openWebUrl(url: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = url.toUri()
            startActivity(intent)
            binding.root.closeDrawer(binding.navView)
        } catch (e: Exception) {
            android.widget.Toast.makeText(
                this,
                "Web sayfası açılamadı: ${e.message}",
                android.widget.Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}