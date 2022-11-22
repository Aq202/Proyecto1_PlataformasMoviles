package com.example.proyecto_final_apps.ui.fragments.signUp

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.proyecto_final_apps.R
import com.example.proyecto_final_apps.databinding.FragmentSignUpBinding
import com.example.proyecto_final_apps.ui.activity.LoadingViewModel
import com.example.proyecto_final_apps.ui.activity.MainActivity
import com.example.proyecto_final_apps.ui.activity.UserViewModel
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.datepicker.MaterialDatePicker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.util.*

@AndroidEntryPoint
class SignUpFragment : Fragment() {

    private val signUpViewModel: SignUpViewModel by viewModels()
    private lateinit var binding: FragmentSignUpBinding
    private var profilePicPath: String? = ""
    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        result: ActivityResult ->
        val resultCode = result.resultCode
        val data = result.data

        if (resultCode == Activity.RESULT_OK) {
            //Image Uri will not be null for RESULT_OK
            val fileUri = result.data?.data
            if (resultCode == Activity.RESULT_OK) {
                //Image Uri will not be null for RESULT_OK
                val fileUri = data?.data!!

                binding.imageViewSignUpFragmentBanner.setImageURI(fileUri)
                binding.imageViewSignUpFragmentBanner.drawable.setTintList(null)

                //Guardar path
                profilePicPath = fileUri.path
            } else if (resultCode == ImagePicker.RESULT_ERROR) {
                Toast.makeText(requireContext(), ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Task Cancelled", Toast.LENGTH_SHORT).show()
            }
        }
    }

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


                        handleStartMainActivityAction()

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

    private fun handleStartMainActivityAction() {
        val intent = Intent(context, MainActivity::class.java)
        startActivity(intent)
        requireActivity().finish() //finalizar activity unlogged
    }

    private fun initEvents(){

        binding.apply {

            textViewSignUpFragmentLoginLabel.setOnClickListener{
                findNavController().navigate(R.id.action_signUpFragment2_to_loginFragment2)
            }

            datePickerSignUpFragmentBirthDate.setOnClickListener {
                val datePicker =
                    MaterialDatePicker.Builder.datePicker()
                        .setTitleText("Fecha de nacimiento")
                        .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                        .build()

                        datePicker.addOnPositiveButtonClickListener {
                            signUpViewModel.setBirthDate(Date(it)) //guardar fecha en viewmodel
                     datePickerSignUpFragmentBirthDate.setText(datePicker.headerText)
                    }

                datePicker.show(requireActivity().supportFragmentManager, "DatePicker")
            }

            signUpFragmentUploadImageButton.setOnClickListener {
                ImagePicker.with(this@SignUpFragment)
                    .cropSquare()
                    .createIntent { intent: Intent ->
                        galleryLauncher.launch(intent)
                    }
            }

            buttonSignUpFragmentSignUp.setOnClickListener {

                signUp()

            }
        }

    }

    private fun signUp() {
        val firstName = binding.textFieldSignUpFragmentFirstName.editText!!.text.toString().trim()
        val lastName = binding.textFieldSignUpFragmentLastName.editText!!.text.toString().trim()
        val email = binding.textFieldSignUpFragmentEmail.editText!!.text.toString().trim()
        val password = binding.textFieldSignUpFragmentPassword.editText!!.text.toString()
        val confirmPass = binding.textFieldSignUpFragmentConfirmPassword.editText!!.text.toString()
        val user = binding.textFieldSignUpFragmentUser.editText!!.text.toString().trim()
        val birthDate = signUpViewModel.birthDate.value

        signUpViewModel.signUp(
            firstName = firstName,
            lastName = lastName,
            birthDate = birthDate,
            user = user,
            email = email,
            password = password,
            confirmPass = confirmPass,
            profilePicPath = profilePicPath?: ""
        )
    }
}
