package com.example.proyecto_final_apps.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import com.example.proyecto_final_apps.R
import com.example.proyecto_final_apps.data.AccountData
import com.example.proyecto_final_apps.databinding.FragmentNewDebtBinding

class NewDebtFragment : Fragment() {
    private lateinit var binding: FragmentNewDebtBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNewDebtBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setDropLists()
        setListeners()
    }

    private fun setDropLists() {
        val accounts = arrayListOf<String>()
        val operationTypes = listOf("Ingreso","Egreso")
        AccountData.accounts.forEach{
            accounts.add(it.title)
        }
        val adapterAccount = ArrayAdapter(requireContext(), R.layout.list_item, accounts)
        val adapterOperation = ArrayAdapter(requireContext(), R.layout.list_item, operationTypes)
        binding.autoCompleteViewNewDebtFragmentSourceAccount.setAdapter(adapterAccount)
        binding.autoCompleteViewNewDebtFragmentOperationType.setAdapter(adapterOperation)
    }

    private fun setListeners() {
        binding.autoCompleteViewNewDebtFragmentSourceAccount.setOnItemClickListener { adapterView, view, i, l ->
            var cuenta = adapterView.getItemAtPosition(i).toString()
            Toast.makeText(requireContext(), "Cuenta: $cuenta", Toast.LENGTH_SHORT).show()
        }
        binding.autoCompleteViewNewDebtFragmentOperationType.setOnItemClickListener { adapterView, view, i, l ->
            var operationType = adapterView.getItemAtPosition(i).toString()
            Toast.makeText(requireContext(), "Cuenta: $operationType", Toast.LENGTH_SHORT).show()
        }
    }
}