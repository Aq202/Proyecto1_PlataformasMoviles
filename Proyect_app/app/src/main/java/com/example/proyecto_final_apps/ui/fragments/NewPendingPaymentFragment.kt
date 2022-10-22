package com.example.proyecto_final_apps.ui.fragments

import android.content.res.ColorStateList
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import androidx.core.view.children
import androidx.navigation.findNavController
import com.example.proyecto_final_apps.R
import com.example.proyecto_final_apps.data.Category
import com.example.proyecto_final_apps.databinding.FragmentNewPendingPaymentBinding
import com.example.proyecto_final_apps.helpers.addChip
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.datepicker.MaterialDatePicker

class NewPendingPaymentFragment : Fragment() {
    private lateinit var binding: FragmentNewPendingPaymentBinding
    private lateinit var checkedCathegory: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNewPendingPaymentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        generateChipGroup()
        setDropLists()
        setListeners()
    }

    private fun setDropLists() {
        val operationTypes = listOf("Ingreso","Egreso")
        val adapterOperation = ArrayAdapter(requireContext(), R.layout.list_item, operationTypes)
        binding.autoCompleteViewNewPendingPaymentFragmentOperationType.setAdapter(adapterOperation)
    }

    private fun updateChips(chipGroup: ChipGroup, checkedChip: Int) {
        chipGroup.children.forEach { chip ->
            chip as Chip
            if(chip.id == checkedChip){
                chip.chipStrokeWidth = 1F
                checkedCathegory = chip.text.toString()
            }
            else
                chip.chipStrokeWidth = 0F
        }
    }

    private fun generateChipGroup() {
        val categories = Category(requireContext()).getCategories()
        categories.forEach{ cathegory ->
            val backgroundCSL = generateCSL(cathegory.color, true)
            val strokeCSL = generateCSL(cathegory.color, false)
            binding.chipGroupNewPendingPaymentFragmentCathegories.addChip(requireContext(), cathegory, backgroundCSL, strokeCSL)
        }
    }

    private fun generateCSL(color: Int, default: Boolean): ColorStateList {
        val states =
            if(default)
                arrayOf(
                    intArrayOf(android.R.attr.state_checked),
                    intArrayOf(-android.R.attr.state_checked)
                )
            else
                arrayOf(
                    intArrayOf(-android.R.attr.state_checked),
                    intArrayOf(android.R.attr.state_checked)
                )
        val colors = intArrayOf(
            if (default) ColorUtils.setAlphaComponent(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.white
                ),0) else ContextCompat.getColor(requireContext(), R.color.white),
            color
        )
        return ColorStateList(states, colors)
    }

    private fun setListeners() {
        val chipGroup = binding.chipGroupNewPendingPaymentFragmentCathegories
        chipGroup.setOnCheckedStateChangeListener { group, checkedIds ->
            updateChips(chipGroup, checkedIds[0])
            Toast.makeText(requireContext(), "Categoria seleccionada: $checkedCathegory", Toast.LENGTH_SHORT).show()
        }
        binding.autoCompleteViewNewPendingPaymentFragmentOperationType.setOnItemClickListener { adapterView, view, i, l ->
            var operationType = adapterView.getItemAtPosition(i).toString()
            Toast.makeText(requireContext(), "Cuenta: $operationType", Toast.LENGTH_SHORT).show()
        }
        binding.datePickerNewPendingPaymentFragmentNotificationDate.setOnClickListener {
            val datePicker =
                MaterialDatePicker.Builder.datePicker()
                    .setTitleText("Fecha seleccionada")
                    .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                    .build()

                datePicker.addOnPositiveButtonClickListener {
                    binding.datePickerNewPendingPaymentFragmentNotificationDate.setText(datePicker.headerText)
                }

            datePicker.show(requireActivity().supportFragmentManager, "DatePicker")
        }

    }
}