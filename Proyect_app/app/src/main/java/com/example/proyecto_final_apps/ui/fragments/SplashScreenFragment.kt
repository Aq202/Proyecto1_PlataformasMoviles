package com.example.proyecto_final_apps.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.proyecto_final_apps.R
import com.example.proyecto_final_apps.databinding.FragmentSplashScreenBinding
import com.example.proyecto_final_apps.ui.activity.UserSessionStatus
import com.example.proyecto_final_apps.ui.activity.UserViewModel
import kotlinx.coroutines.flow.collectLatest

class SplashScreenFragment : Fragment() {

    private lateinit var binding:FragmentSplashScreenBinding
    private val userViewModel:UserViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSplashScreenBinding.inflate(inflater,container, false)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        userViewModel.getUserData(false)
        setObservers()
    }

    private fun setObservers() {
        lifecycleScope.launchWhenStarted {
            userViewModel.userDataStateFlow.collectLatest { state ->
                when(state){
                    is UserSessionStatus.Logged -> {
                        //findNavController().navigate(R.id.action_splashScreenFragment_to_homeFragment)
                    }
                    is UserSessionStatus.NotLogged -> {
                        //findNavController().navigate(R.id.action_splashScreenFragment_to_loginFragment)
                    }
                    else -> {}
                }

            }
        }
    }
}