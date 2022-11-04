package com.example.proyecto_final_apps.ui.fragments.signUp

import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.proyecto_final_apps.R
import com.example.proyecto_final_apps.databinding.FragmentSignUpBinding
import com.example.proyecto_final_apps.ui.activity.UserViewModel
import com.google.android.material.datepicker.MaterialDatePicker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class SignUpFragment : Fragment() {

    private val signUpViewModel: SignUpViewModel by viewModels()
    private val userViewModel: UserViewModel by activityViewModels()
    private lateinit var binding: FragmentSignUpBinding

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
        setObservers()


    }

    private fun setObservers() {
        lifecycleScope.launchWhenStarted {
            signUpViewModel.signUpStateFlow.collectLatest { result ->

                when(result){
                    is SignUpStatus.Loading -> {
                        binding.apply {
                            buttonSignUpFragmentSignUp.visibility = View.INVISIBLE
                            progressIndicatorFragmentSignUp.visibility = View.VISIBLE
                        }
                    }
                    is SignUpStatus.Registered -> {
                        userViewModel.getUserData(false)
                        requireView().findNavController().navigate(R.id.action_signUpFragment_to_homeFragment)
                    }
                    is SignUpStatus.Error -> {
                        Toast.makeText(requireContext(), result.error, Toast.LENGTH_LONG).show()
                        binding.apply {
                            buttonSignUpFragmentSignUp.visibility = View.VISIBLE
                            progressIndicatorFragmentSignUp.visibility = View.GONE
                        }
                    }
                    else -> {}
                }
            }

        }
    }

    private fun initEvents(){

        binding.apply {

            textViewSignUpFragmentLoginLabel.setOnClickListener{
                findNavController().navigate(R.id.action_signUpFragment_to_loginFragment)
            }

            buttonSignUpFragmentSignUp.setOnClickListener {
                findNavController().navigate(R.id.action_signUpFragment_to_homeFragment)
            }

            datePickerSignUpFragmentBirthDate.setOnClickListener {
                val datePicker =
                    MaterialDatePicker.Builder.datePicker()
                        .setTitleText("Fecha de nacimiento")
                        .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                        .build()

                datePicker.addOnPositiveButtonClickListener {
                    datePickerSignUpFragmentBirthDate.setText(datePicker.headerText)
                }
            }

            buttonSignUpFragmentSignUp.setOnClickListener {

                signUp()

            }
        }

    }

    private fun signUp() {
        val firstName = binding.textFieldSignUpFragmentFirstName.editText!!.text.toString()
        val lastName = binding.textFieldSignUpFragmentLastName.editText!!.text.toString()
        val email = binding.textFieldSignUpFragmentEmail.editText!!.text.toString()
        val password = binding.textFieldSignUpFragmentPassword.editText!!.text.toString()
        val confirmPass = binding.textFieldSignUpFragmentConfirmPassword.editText!!.text.toString()
        val user = binding.textFieldSignUpFragmentUser.editText!!.text.toString()
        val birthDate = binding.textFieldSignUpFragmentBirthDate.editText!!.toString()

        signUpViewModel.signUp(
            firstName = firstName,
            lastName = lastName,
            birthDate = birthDate,
            user = user,
            email = email,
            password = password,
            confirmPass = confirmPass
        )
    }
}
