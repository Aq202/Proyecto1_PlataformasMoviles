package com.example.proyecto_final_apps

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.chip.ChipGroup

class NewOperationFragment : Fragment(R.layout.fragment_new_operation) {
    private lateinit var chipGroup: ChipGroup

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        chipGroup = view.findViewById(R.id.chip_group_newOperationFragment_cathegories)
    }

    fun ChipGroup.addChip(){

    }
}