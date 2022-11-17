package com.example.proyecto_final_apps.ui.activity

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.example.proyecto_final_apps.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@SuppressLint("CustomSplashScreen")
@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {

    private val userViewModel:UserViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {

        val splashScreen = installSplashScreen()

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        splashScreen.setKeepOnScreenCondition { true }  //Evitar que se cierre el splash screen

        userViewModel.getUserData(false)
        setObservers()

    }

    private fun setObservers() {

        val context = this

        lifecycleScope.launchWhenStarted {
            userViewModel.userDataStateFlow.collectLatest { state ->
                when(state){
                    is UserSessionStatus.Logged -> {
                        val intent = Intent(context, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                    is UserSessionStatus.NotLogged -> {
                        val intent = Intent(context, UnloggedActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                    else -> {}
                }

            }
        }
    }
}