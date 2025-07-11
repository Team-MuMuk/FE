package com.example.mumuk.ui

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import com.example.mumuk.R
import com.example.mumuk.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        val navOptions = NavOptions.Builder()
            .setLaunchSingleTop(true)
            .setPopUpTo(R.id.nav_graph, false)
            .build()

        binding.bottomNavView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    navController.navigate(R.id.navigation_home, null, navOptions)
                    true
                }
                R.id.navigation_category -> {
                    navController.navigate(R.id.navigation_category, null, navOptions)
                    true
                }
                R.id.navigation_search -> {
                    navController.navigate(R.id.navigation_search, null, navOptions)
                    true
                }
                R.id.navigation_my_page -> {
                    navController.navigate(R.id.navigation_my_page, null, navOptions)
                    true
                }
                else -> false
            }
        }
    }

    fun hideBottomNav() {
        binding.bottomNavView.visibility = View.GONE
    }

    fun showBottomNav() {
        binding.bottomNavView.visibility = View.VISIBLE
    }
}