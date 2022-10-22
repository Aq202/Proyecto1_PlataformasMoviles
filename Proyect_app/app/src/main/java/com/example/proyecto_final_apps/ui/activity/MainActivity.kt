package com.example.proyecto_final_apps.ui.activity

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.GravityCompat
import androidx.core.view.isVisible
import androidx.drawerlayout.widget.DrawerLayout.LOCK_MODE_LOCKED_CLOSED
import androidx.drawerlayout.widget.DrawerLayout.LOCK_MODE_UNLOCKED
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.example.proyecto_final_apps.R
import com.example.proyecto_final_apps.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.flow.collectLatest


class MainActivity : AppCompatActivity() {


    private lateinit var navController: NavController
    private lateinit var navBar: BottomNavigationView
    private lateinit var binding: ActivityMainBinding

    private var searchViewInitialized = false

    private val toolbarViewModel: ToolbarViewModel by viewModels()
    private val bottomNavigationViewModel:BottomNavigationViewModel by viewModels()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)


        //configuar toolbar
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentContainer) as NavHostFragment
        navController = navHostFragment.navController

        val appbarConfig =
            AppBarConfiguration(setOf(R.id.loginFragment, R.id.homeFragment), binding.drawerLayout)
        binding.toolbar.setupWithNavController(navController, appbarConfig)
        binding.navView.setupWithNavController(navController)

        listenToNavGraphChanges()
        listenToNavDrawerChanges()
        setObservers()
    }

    private fun setObservers() {
        lifecycleScope.launchWhenStarted {
            bottomNavigationViewModel.selectedItem.collectLatest { item ->
                binding.bottomNavigationBar.menu.findItem(item.itemId).isChecked = true
            }
        }
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
            when (it.itemId) {
                R.id.item_bottomNav_home -> navController.navigate(R.id.action_toHome)
                R.id.item_bottomNav_newOperation -> navController.navigate(R.id.action_toTabLayoutFragment)
                R.id.item_bottomNav_contacts -> navController.navigate(R.id.action_toContacts)
            }
            true
        }
    }


    private fun listenToNavGraphChanges() {
        navController.currentDestination
        //Detectar cambios en la navegación
        navController.addOnDestinationChangedListener { _, destination, _ ->

            binding.apply {
                //mostrar y ocultar el toolbar y bottomNavBar
                if (destination.id in setOf(R.id.loginFragment, R.id.signUpFragment)) {
                    toolbar.isVisible = false
                    bottomNavigationBar.isVisible = false
                    binding.drawerLayout.setDrawerLockMode(LOCK_MODE_LOCKED_CLOSED)
                } else {
                    toolbar.isVisible = true
                    bottomNavigationBar.isVisible = true
                    binding.drawerLayout.setDrawerLockMode(LOCK_MODE_UNLOCKED)

                }

                //ejecuta onCreateMenuItems
                invalidateOptionsMenu()

            }
        }

    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        return super.onPrepareOptionsMenu(menu)

    }


    /**
     * Manejo de la barra de busqueda
     */
    override fun onCreateOptionsMenu(menu: Menu): Boolean {

        menuInflater.inflate(R.menu.toolbar_menu, menu)
        val search = menu.findItem(R.id.searchBar)

        if (navController.currentDestination?.id == R.id.contactsFragment) {

            val searchView = search.actionView as SearchView
            searchView.queryHint = "Search"

            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {

                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean {

                    toolbarViewModel.triggerSearchFlow(newText ?: "")
                    return true
                }
            })


            search.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
                override fun onMenuItemActionExpand(item: MenuItem?): Boolean {
                    // TODO: do something...
                    println("DIEGO: EXPAND")

                    return true
                }

                override fun onMenuItemActionCollapse(item: MenuItem?): Boolean {
                    // TODO: do something...
                    println("DIEGO: COLLAPSE")
                    toolbarViewModel.triggerSearchFlow("")

                    return true
                }
            })





        } else search.isVisible = false



        return super.onCreateOptionsMenu(menu)
    }




}