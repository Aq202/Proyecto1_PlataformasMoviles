package com.example.proyecto_final_apps.ui.fragments.editProfile

import android.app.Activity
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.proyecto_final_apps.R
import com.example.proyecto_final_apps.databinding.FragmentEditProfileBinding
import com.example.proyecto_final_apps.ui.activity.LoadingViewModel
import com.example.proyecto_final_apps.ui.activity.UserSessionStatus
import com.example.proyecto_final_apps.ui.activity.UserViewModel
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.datepicker.MaterialDatePicker
import kotlinx.coroutines.flow.collectLatest
import java.util.*


class EditProfileFragment : Fragment(R.layout.fragment_edit_profile) {

    private lateinit var binding: FragmentEditProfileBinding

    private val editProfileViewModel: EditProfileViewModel by viewModels()
    private val userViewModel: UserViewModel by activityViewModels()
    private val loadingViewModel: LoadingViewModel by activityViewModels()
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

                binding.imageViewEditProfileFragmentPicture.setImageURI(fileUri)
                binding.imageViewEditProfileFragmentPicture.drawable.setTintList(null)

                //Guardar path
                profilePicPath = fileUri.path
                Toast.makeText(requireContext(), profilePicPath, Toast.LENGTH_LONG).show()
            } else if (resultCode == ImagePicker.RESULT_ERROR) {
                Toast.makeText(requireContext(), ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Task Cancelled", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEditProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userViewModel.getUserData(false)
        val sessionStatus = userViewModel.userDataStateFlow.value

        if (sessionStatus is UserSessionStatus.Logged){
            val currentUserData = sessionStatus.data
            binding.apply {
                textFieldEditProfileFragmentFirstName.editText!!.setText(currentUserData.name)
                textFieldEditProfileFragmentLastName.editText!!.setText(currentUserData.lastName)
                textFieldEditProfileFragmentBirthDate.editText!!.setText(currentUserData.birthDate)
                textFieldEditProfileFragmentUser.editText!!.setText(currentUserData.alias)
                textFieldEditProfileFragmentEmail.editText!!.setText(currentUserData.email)

            }
        }

        setListeners()
        setObservers()

    }

    private fun setObservers() {
        lifecycleScope.launchWhenStarted {
            editProfileViewModel.editProfileStateFlow.collectLatest { result->
                when(result) {
                    is EditProfileStatus.Loading -> {
                        binding.apply {
                            buttonEditProfileFragmentSaveChanges.visibility = View.INVISIBLE
                            progressIndicatorFragmentEditProfile.visibility = View.VISIBLE
                        }
                    }
                    is EditProfileStatus.Error -> {
                        Toast.makeText(requireContext(), result.error, Toast.LENGTH_LONG).show()
                        binding.apply {
                            buttonEditProfileFragmentSaveChanges.visibility = View.VISIBLE
                            buttonEditProfileFragmentSaveChanges.visibility = View.GONE
                        }
                    }
                    is EditProfileStatus.Updated -> {
                        userViewModel.getUserData(true)
                        requireView().findNavController().navigate(R.id.action_toUserProfile)
                    }
                    else -> {}
                }
            }
        }
    }

    private fun setListeners() {

        binding.apply{

            datePickerEditProfileFragmentBirthDate.setOnClickListener {
                val datePicker =
                    MaterialDatePicker.Builder.datePicker()
                        .setTitleText("Fecha de nacimiento")
                        .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                        .build()

                datePicker.addOnPositiveButtonClickListener {
                    editProfileViewModel.setBirthDate(Date(it))
                    datePickerEditProfileFragmentBirthDate.setText(datePicker.headerText)
                }

                datePicker.show(requireActivity().supportFragmentManager, "DatePicker")
            }

            buttonEditProfileFragmentSaveChanges.setOnClickListener {
                saveChanges()
            }
        }

    }

    private fun saveChanges() {
        val firstName = binding.textFieldEditProfileFragmentFirstName.editText!!.text.toString()
        val lastName = binding.textFieldEditProfileFragmentLastName.editText!!.text.toString()
        val email = binding.textFieldEditProfileFragmentEmail.editText!!.text.toString()
        val password = binding.textFieldEditProfileFragmentPassword.editText!!.text.toString()
        val confirmPass = binding.textFieldEditProfileFragmentConfirmPassword.editText!!.text.toString()
        val username = binding.textFieldEditProfileFragmentUser.editText!!.text.toString()
        val birthDate = editProfileViewModel.birthDate.value!!

        editProfileViewModel.editProfile(
            firstName = firstName,
            lastName = lastName,
            birthDate = birthDate,
            user = username,
            email = email,
            password = password,
            confirmPass = confirmPass,
            profilePicPath = profilePicPath?: ""
        )
    }

}