package com.example.proyecto_final_apps.presentation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.View
import androidx.core.view.GravityCompat
import androidx.core.view.isVisible
import androidx.drawerlayout.widget.DrawerLayout.LOCK_MODE_LOCKED_CLOSED
import androidx.drawerlayout.widget.DrawerLayout.LOCK_MODE_UNLOCKED
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.example.proyecto_final_apps.R
import com.example.proyecto_final_apps.databinding.ActivityMainBinding
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView

interface DrawerLocker{
    fun setDrawerLocked(shouldLock:Boolean);
}

class MainActivity : AppCompatActivity() {



    private lateinit var navController: NavController

    private lateinit var binding: ActivityMainBinding



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)




        setSupportActionBar(binding.toolbar)




        //configuar toolbar
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainer) as NavHostFragment
        navController = navHostFragment.navController

        val appbarConfig = AppBarConfiguration(setOf(R.id.loginFragment, R.id.homeFragment), binding.drawerLayout)
        binding.toolbar.setupWithNavController(navController, appbarConfig)
        binding.navView.setupWithNavController(navController)

        listenToNavGraphChanges()
    }

    private fun listenToNavGraphChanges(){

        //Detectar cambios en la navegación
        navController.addOnDestinationChangedListener { _, destination, _ ->

            binding.apply {
            //mostrar y ocultar el toolbar y bottomNavBar
            if(destination.id in setOf(R.id.loginFragment, R.id.signUpFragment)){
                toolbar.isVisible = false
                bottomNavigationBar.isVisible = false
                binding.drawerLayout.setDrawerLockMode(LOCK_MODE_LOCKED_CLOSED)
            }else{
                toolbar.isVisible = true
                bottomNavigationBar.isVisible = true
                binding.drawerLayout.setDrawerLockMode(LOCK_MODE_UNLOCKED)

            }
        }
        }

    }
}