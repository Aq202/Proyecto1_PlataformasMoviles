package com.example.proyecto_final_apps.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.example.proyecto_final_apps.R
import com.example.proyecto_final_apps.ui.activity.BottomNavigationViewModel

class NewAccountFragment : Fragment(R.layout.fragment_new_account) {


    private val bottomNavigationViewModel: BottomNavigationViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_new_account, container, false)
    }

    override fun onResume() {
        super.onResume()
        selectCurrentBottomNavigationItem()
    }

    private fun selectCurrentBottomNavigationItem(){
        bottomNavigationViewModel.setSelectedItem(BottomNavigationViewModel.BottomNavigationItem.HOME)
    }

}