package com.example.proyecto_final_apps.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.children
import com.example.proyecto_final_apps.R
import com.example.proyecto_final_apps.data.Category
import com.example.proyecto_final_apps.databinding.FragmentNewOperationBinding
import com.example.proyecto_final_apps.helpers.addChip
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup

class NewOperationFragment : Fragment(R.layout.fragment_new_operation) {
    private lateinit var binding: FragmentNewOperationBinding
    private lateinit var checkedCathegory: String 

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNewOperationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        generateChipGroup()
        setListeners()
    }

    private fun setListeners() {
        var chipGroup = binding.chipGroupNewOperationFragmentCathegories
        chipGroup.setOnCheckedStateChangeListener { group, checkedIds ->
            updateChips(chipGroup, checkedIds[0])
            Toast.makeText(requireContext(), "Categoria seleccionada: $checkedCathegory", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateChips(chipGroup: ChipGroup, checkedChip: Int) {
        chipGroup.children.forEach { chip ->
            chip as Chip
            if(chip.id == checkedChip){
                chip.isChecked = true
                checkedCathegory = chip.text.toString()
            }
            else
                chip.isChecked = false
        }
    }

    private fun generateChipGroup() {
        var categories = Category(requireContext()).getCategories()
        categories.forEach{ cathegory ->
            binding.chipGroupNewOperationFragmentCathegories.addChip(requireContext(), cathegory)
        }
    }
}