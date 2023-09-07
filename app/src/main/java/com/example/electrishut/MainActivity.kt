package com.example.electrishut

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.example.electrishut.dashboard.ForegroundService
import com.example.electrishut.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private lateinit var navHostFragment: NavHostFragment
    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO) // prevent the app from using dark mode
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.navigation) as NavHostFragment
        navController = navHostFragment.navController

        val bottomNavView: BottomNavigationView = binding.bottomNavigationView

        navController.addOnDestinationChangedListener { _, destination, _ ->
            bottomNavView.visibility = if (destination.id == R.id.mobileNumber || destination.id == R.id.about || destination.id == R.id.abtThresholds) {
                View.GONE
            } else {
                View.VISIBLE
            }
        }

        NavigationUI.setupWithNavController(bottomNavView, navController)

        supportActionBar?.apply {
            // Set the logo and display it in the toolbar
            setLogo(R.drawable.applogo)
            setDisplayUseLogoEnabled(true)
            setDisplayShowHomeEnabled(true)
        }

        val serviceIntent = Intent(this, ForegroundService::class.java)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent)
        } else {
            startService(serviceIntent)
        }

        foregroundServiceRunning()
    }

    fun foregroundServiceRunning(): Boolean {
        val activityManager = getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager
        if (activityManager != null) {
            for (service in activityManager.getRunningServices(Integer.MAX_VALUE)) {
                if (ForegroundService::class.java.name == service.service.className) {
                    return true
                }
            }
        }
        return false
    }
    override fun onDestroy() {
        super.onDestroy()
        Log.d("MainActivity", "Activity destroyed")
    }
}