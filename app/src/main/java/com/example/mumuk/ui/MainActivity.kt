package com.example.mumuk.ui

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.example.mumuk.R
import com.example.mumuk.databinding.ActivityMainBinding
import com.example.mumuk.ui.home.HomeFragment

class MainActivity : AppCompatActivity(), HomeFragment.BottomNavSelector {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        binding.bottomNavView.setOnItemSelectedListener { item ->
            if (item.itemId == binding.bottomNavView.selectedItemId) {

                val currentFragment = navHostFragment.childFragmentManager.fragments
                    .find { it is HomeFragment } as? HomeFragment
                currentFragment?.forceRefresh()

                navController.popBackStack(item.itemId, inclusive = false)
                return@setOnItemSelectedListener true
            }

            val builder = NavOptions.Builder()
                .setLaunchSingleTop(true)
                .setPopUpTo(navController.graph.startDestinationId, false)

            val options = builder.build()
            try {
                navController.navigate(item.itemId, null, options)
                true
            } catch (e: IllegalArgumentException) {
                false
            }
        }

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.healthManagementFragment,
                     R.id.healthCompleteFragment-> {
                    hideBottomNav()
                }
                else -> {
                    showBottomNav()
                }
            }
        }

        if (intent.getBooleanExtra("go_health_management", false)) {
            // 이미 백스택에 있을 수도 있으니, 중복 이동이 안되게 조심
            // 현재 destination이 healthManagementFragment가 아니라면 이동
            if (navController.currentDestination?.id != R.id.healthManagementFragment) {
                navController.navigate(R.id.healthManagementFragment)
            }
        }
    }

    override fun selectBottomNavItem(itemId: Int) {
        binding.bottomNavView.selectedItemId = itemId
    }

    fun hideBottomNav() {
        binding.bottomNavView.visibility = View.GONE
    }

    fun showBottomNav() {
        binding.bottomNavView.visibility = View.VISIBLE
    }
}