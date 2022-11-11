package com.example.proyecto_final_apps.ui.activity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyecto_final_apps.data.Resource
import com.example.proyecto_final_apps.data.local.entity.UserModel
import com.example.proyecto_final_apps.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class UserSessionStatus{
    class Logged(val data: UserModel):UserSessionStatus()
    object NotLogged:UserSessionStatus()
    object Default:UserSessionStatus()
}

@HiltViewModel
class UserViewModel @Inject constructor(
    private val repository: UserRepository
): ViewModel() {

    private val _userDataStateFlow: MutableStateFlow<UserSessionStatus> = MutableStateFlow(
        UserSessionStatus.Default)
    val userDataStateFlow: StateFlow<UserSessionStatus> = _userDataStateFlow

    fun getUserData(remote:Boolean){

        viewModelScope.launch {
            when(val result = repository.getUserInSessionData(remote)){
                is Resource.Success -> {
                    _userDataStateFlow.value = UserSessionStatus.Logged(result.data)
                }
                else -> {
                    _userDataStateFlow.value = UserSessionStatus.NotLogged
                }
            }
        }
    }

    fun logout(){
        viewModelScope.launch {
            repository.logout()
            _userDataStateFlow.value = UserSessionStatus.NotLogged
        }
    }

}