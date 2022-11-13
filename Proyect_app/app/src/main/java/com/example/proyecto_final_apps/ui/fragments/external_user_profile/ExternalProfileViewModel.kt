package com.example.proyecto_final_apps.ui.fragments.external_user_profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyecto_final_apps.data.Resource
import com.example.proyecto_final_apps.data.local.entity.ContactFullDataModel
import com.example.proyecto_final_apps.data.local.entity.ContactModel
import com.example.proyecto_final_apps.data.local.entity.UserModel
import com.example.proyecto_final_apps.data.repository.ContactRepository
import com.example.proyecto_final_apps.data.repository.ContactRepositoryImp
import com.example.proyecto_final_apps.data.repository.UserRepository
import com.example.proyecto_final_apps.ui.util.Status
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExternalProfileViewModel @Inject constructor(private val userRepository: UserRepository, private val contactRepository: ContactRepository) : ViewModel() {

    private val _userData = MutableStateFlow<Status<UserModel>>(Status.Loading())
    val userData: StateFlow<Status<UserModel>> = _userData

    private val _fragmentState = MutableStateFlow<Status<Boolean>>(Status.Default())
    val fragmentState: StateFlow<Status<Boolean>> = _fragmentState

    fun setSuccessFragmentState(){
        _fragmentState.value = Status.Success(true)
    }
    fun setErrorFragmentState(){
        _fragmentState.value = Status.Error("")
    }

    suspend fun getUserData(userId:String, forceUpdate:Boolean){

            val result = userRepository.getUserData(userId, forceUpdate)
            if(result is Resource.Success) _userData.value = Status.Success(result.data)
            else _userData.value = Status.Error(result.message ?: "")

    }

    fun addAsContact(userId:String):Flow<Status<ContactModel>>{
        return flow {

            emit(Status.Loading())
            val result = contactRepository.newContact(userId)

            if(result is Resource.Success) emit(Status.Success(result.data))
            else emit(Status.Error(result.message ?: ""))


        }
    }



}