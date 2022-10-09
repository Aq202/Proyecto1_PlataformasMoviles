package com.example.proyecto_final_apps.presentation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.view.isVisible
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.example.proyecto_final_apps.R
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView


class MainActivity : AppCompatActivity() {

    private lateinit var toolbar: MaterialToolbar
    private lateinit var bottomNavBar: BottomNavigationView
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        toolbar = findViewById(R.id.toolbar)
        bottomNavBar = findViewById(R.id.bottomNavigationBar)

        //configuar toolbar
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainer) as NavHostFragment

        navController = navHostFragment.navController

        val appbarConfig = AppBarConfiguration(setOf(R.id.loginFragment, R.id.homeFragment))
        toolbar.setupWithNavController(navController, appbarConfig)

        listenToNavGraphChanges()
    }

    private fun listenToNavGraphChanges(){

        //Detectar cambios en la navegación
        navController.addOnDestinationChangedListener { _, destination, _ ->

            //mostrar y ocultar el toolbar y bottomNavBar
            if(destination.id in setOf(R.id.loginFragment)){
                toolbar.isVisible = false
                bottomNavBar.isVisible = false
            }else{
                toolbar.isVisible = true
                bottomNavBar.isVisible = true
            }

            //home
            if(destination.id == R.id.homeFragment){
                toolbar.setNavigationIcon(R.drawable.ic_hamburger_menu)

            }else{
                toolbar.navigationIcon = null;
            }

        }

    }
}