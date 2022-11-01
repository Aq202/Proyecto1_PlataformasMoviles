package com.example.proyecto_final_apps.ui.fragments.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyecto_final_apps.data.Resource
import com.example.proyecto_final_apps.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class LoginStatus(){
    object Default:LoginStatus()
    object Loading:LoginStatus()
    object Logged:LoginStatus()
    class Error(val error:String):LoginStatus()

}
@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: UserRepository
) :ViewModel(){

    private val _loginStateFlow : MutableStateFlow<LoginStatus> = MutableStateFlow(LoginStatus.Default)
    val loginStateFlow:StateFlow<LoginStatus> = _loginStateFlow

    fun login(user:String, password:String){

        _loginStateFlow.value = LoginStatus.Loading

        viewModelScope.launch {

            when(val result = repository.login(user=user, password)){
                is Resource.Success -> {
                    _loginStateFlow.value = LoginStatus.Logged
                }
                else -> {
                    _loginStateFlow.value = LoginStatus.Error(result.message ?: "Ocurrió un error.")
                }
            }
        }
    }
}