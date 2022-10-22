package com.example.proyecto_final_apps.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.proyecto_final_apps.R
import com.example.proyecto_final_apps.databinding.FragmentSignUpBinding
import com.google.android.material.datepicker.MaterialDatePicker


class SignUpFragment : Fragment() {


    private lateinit var binding:FragmentSignUpBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSignUpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initEvents()


    }

    private fun initEvents(){

        binding.apply {

            textViewSignUpFragmentLoginLabel.setOnClickListener{
                findNavController().navigate(R.id.action_signUpFragment_to_loginFragment)
            }

            buttonSignUpFragmentSignUp.setOnClickListener {
                findNavController().navigate(R.id.action_signUpFragment_to_homeFragment)
            }
        }

        binding.datePickerSignUpFragmentBirthDate.setOnClickListener {
            val datePicker =
                MaterialDatePicker.Builder.datePicker()
                    .setTitleText("Fecha de nacimiento")
                    .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                    .build()

            datePicker.addOnPositiveButtonClickListener {
                binding.datePickerSignUpFragmentBirthDate.setText(datePicker.headerText)
            }
        }

    }
}
