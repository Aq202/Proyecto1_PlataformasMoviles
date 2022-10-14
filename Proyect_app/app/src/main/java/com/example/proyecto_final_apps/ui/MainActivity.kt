package com.example.proyecto_final_apps.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.view.GravityCompat
import androidx.core.view.isVisible
import androidx.drawerlayout.widget.DrawerLayout.LOCK_MODE_LOCKED_CLOSED
import androidx.drawerlayout.widget.DrawerLayout.LOCK_MODE_UNLOCKED
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.NavHostFragment.Companion.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.example.proyecto_final_apps.R
import com.example.proyecto_final_apps.databinding.ActivityMainBinding
import com.example.proyecto_final_apps.presentation.fragments.TabLayoutFragment
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView

interface DrawerLocker{
    fun setDrawerLocked(shouldLock:Boolean);
}

class MainActivity : AppCompatActivity() {



    private lateinit var navController: NavController
    private lateinit var navBar: BottomNavigationView
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
        listenToNavDrawerChanges()
    }

    private fun listenToNavDrawerChanges() {
        val context = this
        binding.navView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.sideNav_item_profile -> {
                    navController.navigate(R.id.action_toUserProfile)
                    binding.drawerLayout.closeDrawer(GravityCompat.START)
                    false
                }
                else -> false
            }
        }
        setListeners()
    }

    private fun setListeners() {
        binding.bottomNavigationBar.setOnItemSelectedListener {
            when(it.itemId){
                R.id.item_bottomNav_newOperation -> setCurrentFragment(TabLayoutFragment())
            }
            true
        }
    }

    private fun setCurrentFragment(fragment: Fragment) {
        supportFragmentManager.commit {
            setReorderingAllowed(true)
            replace(R.id.fragmentContainer, fragment)
        }
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