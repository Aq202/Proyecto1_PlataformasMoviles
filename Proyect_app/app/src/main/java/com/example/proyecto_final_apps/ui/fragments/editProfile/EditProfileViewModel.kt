package com.example.proyecto_final_apps.ui.fragments.editProfile

import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyecto_final_apps.data.Resource
import com.example.proyecto_final_apps.data.local.entity.UserModel
import com.example.proyecto_final_apps.data.repository.UserRepository
import com.example.proyecto_final_apps.helpers.DateParse.getDayValue
import com.example.proyecto_final_apps.helpers.DateParse.getMonthValue
import com.example.proyecto_final_apps.helpers.DateParse.getYearValue
import com.example.proyecto_final_apps.ui.activity.UserViewModel
import androidx.fragment.app.activityViewModels
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

sealed class EditProfileStatus(){
    object Default: EditProfileStatus()
    object Loading: EditProfileStatus()
    object Updated: EditProfileStatus()
    class Error(val error: String): EditProfileStatus()
}

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val repository: UserRepository
): ViewModel(){



    private val _editProfileStateFlow: MutableStateFlow<EditProfileStatus> = MutableStateFlow(EditProfileStatus.Default)
    val editProfileStateFlow : StateFlow<EditProfileStatus> = _editProfileStateFlow

    private val _birthDate:MutableStateFlow<String?> = MutableStateFlow(null)
    val birthDate:StateFlow<String?> = _birthDate

    fun loadCurrentUserData(): UserModel{
        _editProfileStateFlow.value = EditProfileStatus.Loading
        user =
    }

    fun setBirthDate(birthDate: Date){
        //Lo guarda en el formato MM/DD/YYYY
        _birthDate.value = "${birthDate.getMonthValue()}/${birthDate.getDayValue()}/${birthDate.getYearValue()}"
    }

    fun updateUserData(){
        _editProfileStateFlow.value = EditProfileStatus.Loading

        viewModelScope.launch {
            when(val result = repository.getUserData()){
                is Resource.Success -> {
                    _editProfileStateFlow.value = EditProfileStatus.Updated
                }
                else -> {
                    _editProfileStateFlow.value = EditProfileStatus.Error(result.message ?: "Ocurrió un error.")

                }
            }
        }
        }

    fun editProfile(
        firstName: String,
        lastName: String,
        birthDate: String,
        user: String,
        email: String,
        password: String,
        confirmPass: String,
        profilePicPath: String
    ){
        _editProfileStateFlow.value = EditProfileStatus.Loading

        viewModelScope.launch {

            if (firstName != "" && lastName != "" && birthDate != "" && user != "" && password != "" && email != "" && confirmPass != ""){
                if (password == confirmPass){

                    when(val result = repository.editProfile(
                        firstName = firstName,
                        lastName = lastName,
                        birthDate = birthDate,
                        user = user,
                        email = email,
                        password = password,
                        profilePicPath = profilePicPath
                    )){
                        is Resource.Success -> {
                            _editProfileStateFlow.value = EditProfileStatus.Updated
                        }
                        else -> {
                            _editProfileStateFlow.value = EditProfileStatus.Error(result.message ?: "Ocurrió un error.")

                        }
                    }
                }
                else{
                    _editProfileStateFlow.value = EditProfileStatus.Error("Las contraseñas ingresadas no coinciden.")
                }
            }
            else{
                _editProfileStateFlow.value = EditProfileStatus.Error("Es necesario completar todos los campos.")
            }
        }
    }

}