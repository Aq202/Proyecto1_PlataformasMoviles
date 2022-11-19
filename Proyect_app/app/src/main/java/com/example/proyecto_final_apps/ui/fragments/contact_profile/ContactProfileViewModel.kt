package com.example.proyecto_final_apps.ui.fragments.contact_profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyecto_final_apps.data.Resource
import com.example.proyecto_final_apps.data.local.entity.ContactFullDataModel
import com.example.proyecto_final_apps.data.local.entity.ContactModel
import com.example.proyecto_final_apps.data.repository.ContactRepository
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
class ContactProfileViewModel @Inject constructor(private val contactRepository: ContactRepository) :
    ViewModel() {

    private val _contactData = MutableStateFlow<Status<ContactFullDataModel>>(Status.Default())
    val contactData: StateFlow<Status<ContactFullDataModel>> = _contactData

    private val _fragmentState = MutableStateFlow<Status<Boolean>>(Status.Default())
    val fragmentState: StateFlow<Status<Boolean>> = _fragmentState

    fun setSuccessFragmentState(){
        _fragmentState.value = Status.Success(true)
    }

    fun setErrorFragmentState(){
        _fragmentState.value = Status.Error("")
    }


    suspend fun getContactData(localContactId: String, forceUpdate: Boolean) {

        val result = contactRepository.getContactData(localContactId, forceUpdate)
        if (result is Resource.Success)
            _contactData.value = Status.Success(result.data)
        else
            _contactData.value = Status.Error(result.message ?: "")
    }


    fun deleteContact(userId:String): Flow<Status<Boolean>> {
        return flow {

            emit(Status.Loading())
            val result = contactRepository.deleteContact(userId)

            if(result is Resource.Success) emit(Status.Success(result.data))
            else emit(Status.Error(result.message ?: ""))


        }
    }




}