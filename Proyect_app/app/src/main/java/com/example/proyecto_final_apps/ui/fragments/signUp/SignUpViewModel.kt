package com.example.proyecto_final_apps.ui.fragments.signUp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyecto_final_apps.data.Resource
import com.example.proyecto_final_apps.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
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

    fun signUp(
        firstName: String,
        lastName: String,
        birthDate: String,
        user: String,
        email: String,
        password: String,
        confirmPass: String,
        profilePicPath: String
    ){
        _signUpStateFlow.value = SignUpStatus.Loading

        viewModelScope.launch {

            if (firstName != "" && lastName != "" && birthDate != "" && user != "" && password != "" && email != "" && confirmPass != "") {
                if (password == confirmPass){

                    //Crear multibody part con el path de la imagen de perfil
                    val profilePicFile = File(profilePicPath)
                    val requestBody = profilePicFile.asRequestBody("image/*".toMediaTypeOrNull())
                    val parts = MultipartBody.Part.createFormData("newimage", profilePicFile.name, requestBody)

                    when(val result = repository.signUp(
                        firstName = firstName,
                        lastName = lastName,
                        birthDate = birthDate,
                        user = user,
                        email = email,
                        password = password,
                        profilePic = parts
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
                _signUpStateFlow.value = SignUpStatus.Error("Es necesario completar todos los campos.")
            }


        }

    }

}