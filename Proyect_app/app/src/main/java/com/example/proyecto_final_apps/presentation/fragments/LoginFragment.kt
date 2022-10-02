package com.example.proyecto_final_apps.presentation.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import com.example.proyecto_final_apps.R
import com.google.android.material.button.MaterialButton

class LoginFragment : Fragment(R.layout.fragment_login) {

    lateinit var loginButton:MaterialButton

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initEvents()


    }

    private fun initEvents(){


    }
}