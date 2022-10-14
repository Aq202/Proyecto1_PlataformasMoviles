package com.example.proyecto_final_apps.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.proyecto_final_apps.R
import com.example.proyecto_final_apps.databinding.FragmentNewOperationBinding
import com.example.proyecto_final_apps.databinding.FragmentTabLayoutBinding
import com.example.proyecto_final_apps.presentation.adapters.TabLayoutAdapter
import com.google.android.material.tabs.TabLayoutMediator

class TabLayoutFragment : Fragment(R.layout.fragment_tab_layout) {
    private lateinit var binding: FragmentTabLayoutBinding
    private var tabTitles = arrayOf("Nuevo","Favoritos")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTabLayoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewPager2TabLayoutFragment.adapter = TabLayoutAdapter(parentFragmentManager,lifecycle)

        TabLayoutMediator(binding.tabLayoutTabLayoutFragment, binding.viewPager2TabLayoutFragment){
            tab, position ->
                tab.text = tabTitles[position]
        }.attach()


    }
}