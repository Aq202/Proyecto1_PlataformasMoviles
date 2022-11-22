package com.example.proyecto_final_apps.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.GravityCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import coil.load
import coil.request.CachePolicy
import com.example.proyecto_final_apps.R
import com.example.proyecto_final_apps.data.local.entity.UserModel
import com.example.proyecto_final_apps.data.socket.SocketClient
import com.example.proyecto_final_apps.databinding.ActivityMainBinding
import com.example.proyecto_final_apps.databinding.NavigationDrawerHeaderBinding
import com.example.proyecto_final_apps.ui.dialogs.LoadingDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {


    private lateinit var navController: NavController
    private lateinit var binding: ActivityMainBinding
    private val loadingDialog = LoadingDialog()

    //Drawer layout bindint
    lateinit var headerView: View
    lateinit var navigationDrawerHeaderBinding: NavigationDrawerHeaderBinding


    private val toolbarViewModel: ToolbarViewModel by viewModels()
    private val bottomNavigationViewModel: BottomNavigationViewModel by viewModels()
    private val mainUserViewModel: UserViewModel by viewModels()
    private val loadingViewModel: LoadingViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar!!.setDisplayShowTitleEnabled(false) //Ocultar titulo en toolbar

        configureNavigation()
        listenToNavDrawerChanges()
        listenToNavGraphChanges()
        setObservers()

        //Binding del drawer layout
        headerView = binding.navView.getHeaderView(0)
        navigationDrawerHeaderBinding = NavigationDrawerHeaderBinding.bind(headerView)

    }


    private fun configureNavigation() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentContainer) as NavHostFragment
        navController = navHostFragment.navController

        val appbarConfig =
            AppBarConfiguration(
                setOf(
                    R.id.homeFragment,
                    R.id.newActionFragment,
                    R.id.contactsFragment
                ), binding.drawerLayout
            )
        binding.toolbar.setupWithNavController(navController, appbarConfig)
        binding.navView.setupWithNavController(navController)
    }

    private fun pruebasSocket() {
        SocketClient.setSocket()
        SocketClient.connect()

        val mSocket = SocketClient.getSocket()

        mSocket.on("global") {
            println("socket message received ${it[0]}")
        }


    }

    private fun setObservers() {
        lifecycleScope.launchWhenStarted {
            bottomNavigationViewModel.selectedItem.collectLatest { item ->
                binding.bottomNavigationBar.menu.findItem(item.itemId).isChecked = true
            }

        }

        lifecycleScope.launchWhenStarted {

            mainUserViewModel.userDataStateFlow.collectLatest { status ->
                when (status) {
                    is UserSessionStatus.Logged -> {
                        addSideBarInfo(status.data)
                    }

                    else -> {}
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            loadingViewModel.isLoading.collectLatest { isLoading ->
                manageLoadingComponent(isLoading)
            }
        }
    }

    private fun addSideBarInfo(data: UserModel) {
        val txtName: TextView = navigationDrawerHeaderBinding.textViewSideNavBarName
        val txtAlias: TextView = navigationDrawerHeaderBinding.textViewSideNavBarAlias
        val profilePic: ImageView = navigationDrawerHeaderBinding.imageViewSideNavBarProfilePic

        txtName.text = getString(R.string.fullName_template, data.name, data.lastName)
        txtAlias.text = getString(R.string.alias_format, data.alias)
        profilePic.load(data.imageUrl) {
            placeholder(R.drawable.ic_default_user)
            error(R.drawable.ic_default_user)
            diskCachePolicy(CachePolicy.ENABLED)
        }
    }

    private fun listenToNavDrawerChanges() {
        binding.navView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.sideNav_item_profile -> {
                    navController.navigate(R.id.action_toUserProfile)
                    binding.drawerLayout.closeDrawer(GravityCompat.START)
                    false
                }
                R.id.sideNav_item_logout -> {
                    //Logout action

                    handleLogoutAction()

                    false
                }
                else -> false
            }
        }
        setListeners()
    }

    private fun listenToNavGraphChanges() {
        navController.addOnDestinationChangedListener(NavController.OnDestinationChangedListener { _, _, _ ->

            //Ocultar toolbar al cambiar fragment
            loadingViewModel.hideLoadingDialog()
        })
    }

    private fun handleLogoutAction() {
        mainUserViewModel.logout()
        binding.drawerLayout.closeDrawer(GravityCompat.START)

        //Moverse al activity unlogged
        val intent = Intent(this, UnloggedActivity::class.java)
        startActivity(intent)
        finish() //Finalizar el activity actual
    }

    private fun setListeners() {
        binding.bottomNavigationBar.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.item_bottomNav_home -> navController.navigate(R.id.action_toHome)
                R.id.item_bottomNav_newOperation -> navController.navigate(R.id.action_toNewActionFragment)
                R.id.item_bottomNav_contacts -> navController.navigate(R.id.action_toContacts)
            }
            true
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
        val delete = menu.findItem(R.id.deleteOperation)
        var edit = menu.findItem(R.id.editOperation)
        delete.isVisible = false
        edit.isVisible = false

        if (navController.currentDestination?.id == R.id.contactsFragment) {

            val searchView = search.actionView as SearchView
            searchView.queryHint = "Search"

            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    toolbarViewModel.triggerSearchFlow(query ?: "")
                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean {

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

    private fun manageLoadingComponent(isLoading: Boolean) {
        if (isLoading) {
            //Show loading dialog
            if (loadingDialog.isAdded) loadingDialog.dismiss()
            if (!loadingDialog.isAdded) loadingDialog.show(supportFragmentManager, "Loading")
        } else {
            lifecycleScope.launchWhenStarted {
                delay(300)
                if (loadingDialog.isAdded) loadingDialog.dismiss()
            }
        }
    }


}