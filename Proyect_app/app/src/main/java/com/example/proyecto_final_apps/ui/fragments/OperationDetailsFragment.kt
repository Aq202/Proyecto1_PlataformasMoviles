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

        setListeners()
    }

    private fun setListeners() {
        binding.buttonOperationDetailsFragmentEdit.setOnClickListener{
            findNavController().navigate(R.id.action_operationDetailsFragment_to_editPendingPaymentFragment)
        }
    }

}