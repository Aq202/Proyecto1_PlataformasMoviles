package com.example.proyecto_final_apps.ui.fragments.signUp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyecto_final_apps.data.Resource
import com.example.proyecto_final_apps.data.local.entity.UserModel
import com.example.proyecto_final_apps.data.repository.UserRepository
import com.example.proyecto_final_apps.helpers.DateParse.getDayValue
import com.example.proyecto_final_apps.helpers.DateParse.getMonthValue
import com.example.proyecto_final_apps.helpers.DateParse.getYearValue
import com.example.proyecto_final_apps.ui.util.Status
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.util.*
import javax.inject.Inject

sealed class SignUpStatus(){
    object Default: SignUpStatus()
    object Loading: SignUpStatus()
    object Registered: SignUpStatus()
    class Error(val error: String): SignUpStatus()
}

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val repository: UserRepository
): ViewModel() {

    private val _signUpStateFlow : MutableStateFlow<SignUpStatus> = MutableStateFlow(SignUpStatus.Default)
    val signUpStateFlow : StateFlow<SignUpStatus> = _signUpStateFlow

    private val _birthDate:MutableStateFlow<String?> = MutableStateFlow(null)
    val birthDate:StateFlow<String?> = _birthDate

    fun setBirthDate(birthDate: Date){
        //Lo guarda en el formato MM/DD/YYYY
        var correctDay = birthDate.getDayValue()+1
        var correctMonth = birthDate.getMonthValue()
        var correctYear = birthDate.getYearValue()
        if (birthDate.getDayValue() == 31){
            correctDay = 1
            correctMonth += 1
            if (birthDate.getMonthValue() == 12){
                correctMonth = 1
                correctYear += 1
            }
        }
        if (birthDate.getDayValue() == 30 && (birthDate.getMonthValue() == 11 || birthDate.getMonthValue() == 4 || birthDate.getMonthValue() == 6 || birthDate.getMonthValue() == 9)){
            correctDay = 1
            correctMonth += 1
        }
        if (birthDate.getDayValue() == 28 && (birthDate.getMonthValue() == 2)){
            correctDay = 1
            correctMonth += 1
        }
        _birthDate.value = "${correctMonth}/${correctDay}/${correctYear}"
    }

    fun signUp(
        firstName: String,
        lastName: String,
        birthDate: String?,
        user: String,
        email: String,
        password: String,
        confirmPass: String,
        profilePicPath: String
    ){
        _signUpStateFlow.value = SignUpStatus.Loading

        viewModelScope.launch {

            if (firstName != "" && lastName != "" && birthDate != null && user != "" && password != "" && email != "" && confirmPass != "") {
                if (profilePicPath != ""){
                    if (password == confirmPass){

                        when(val result = repository.signUp(
                            firstName = firstName,
                            lastName = lastName,
                            birthDate = birthDate,
                            user = user,
                            email = email,
                            password = password,
                            profilePicPath = profilePicPath
                        )){
                            is Resource.Success -> {
                                _signUpStateFlow.value = SignUpStatus.Registered
                            }
                            else -> {
                                _signUpStateFlow.value = SignUpStatus.Error(result.message ?: "Ocurrió un error.")

                            }
                        }
                    }
                    else{
                        _signUpStateFlow.value = SignUpStatus.Error("Las contraseñas ingresadas no coinciden.")
                    }
                }
                else{
                    _signUpStateFlow.value = SignUpStatus.Error("Es necesario elegir una foto de perfil.")
                }

            }
            else{
                _signUpStateFlow.value = SignUpStatus.Error("Es necesario completar todos los campos.")
            }


        }

    }

}