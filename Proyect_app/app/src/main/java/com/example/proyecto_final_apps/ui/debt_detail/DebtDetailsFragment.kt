package com.example.proyecto_final_apps.ui.debt_detail

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.proyecto_final_apps.R
import com.example.proyecto_final_apps.databinding.FragmentDebtDetailsBinding


class DebtDetailsFragment : Fragment() {
   private lateinit var binding:FragmentDebtDetailsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDebtDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }
}