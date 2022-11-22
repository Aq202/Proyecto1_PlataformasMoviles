package com.example.proyecto_final_apps.presentation.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.proyecto_final_apps.ui.fragments.FavoriteOperations.FavoriteOperationsFragment
import com.example.proyecto_final_apps.ui.fragments.newOperation.NewOperationFragment

class TabLayoutAdapter(fragmentManager : FragmentManager, lifecycle : Lifecycle) : FragmentStateAdapter(fragmentManager, lifecycle) {
    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when(position){
            0 -> NewOperationFragment()
            else -> FavoriteOperationsFragment()
        }
    }
}