package com.example.proyecto_final_apps.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.proyecto_final_apps.R
import com.example.proyecto_final_apps.data.AccountData
import com.example.proyecto_final_apps.data.Operation
import com.example.proyecto_final_apps.data.TestOperations
import com.example.proyecto_final_apps.databinding.FragmentOperationDetailsBinding

class OperationDetailsFragment : Fragment() {

    private lateinit var binding:FragmentOperationDetailsBinding
    private val args: OperationDetailsFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOperationDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        insertData()
        setListeners()
    }

    private fun setListeners() {
        binding.buttonOperationDetailsFragmentEdit.setOnClickListener{
            findNavController().navigate(R.id.action_operationDetailsFragment_to_editPendingPaymentFragment)
        }
    }

    private fun insertData() {

        val operationId = args.id

        val operation: Operation = TestOperations(requireContext()).getOperationById(operationId)[0]

        binding.apply {

            textViewOperationDetailsFragmentOriginAccountName.text = AccountData.getAccountById(operation.accountId).title
            textViewOperationDetailsFragmentAmount.text = operation.amount.toString()
            textViewOperationDetailsFragmentOperationType.text = if (operation.active == true) getString(
                            R.string.ingreso) else getString(R.string.egreso)
            textViewOperationDetailsFragmentDescription.text = operation.description ?: getString(R.string.sin_descripcion)
            textViewOperationDetailsFragmentCategory.text = operation.category!!.name
            textViewOperationDetailsFragmentDate.text = operation.date
        }
    }
}