package com.example.proyecto_final_apps.ui.fragments.contacts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyecto_final_apps.data.Resource
import com.example.proyecto_final_apps.data.local.entity.ContactWithUserModel
import com.example.proyecto_final_apps.data.local.entity.UserModel
import com.example.proyecto_final_apps.data.repository.AccountRepository
import com.example.proyecto_final_apps.data.repository.ContactRepository
import com.example.proyecto_final_apps.data.repository.UserRepository
import com.example.proyecto_final_apps.domain.ContactDomain
import com.example.proyecto_final_apps.ui.util.Status
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ContactsViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val contactDomain: ContactDomain
) : ViewModel() {

    private val _contactsList: MutableStateFlow<Status<List<UserModel>>> =
        MutableStateFlow(Status.Default())
    val contactsList: StateFlow<Status<List<UserModel>>> = _contactsList

    private val _externalUsersList: MutableStateFlow<Status<List<UserModel>>> =
        MutableStateFlow(Status.Error(""))
    val externalUserList: StateFlow<Status<List<UserModel>>> = _externalUsersList

    suspend fun getContactsList(forceUpdate: Boolean = false) {

        _contactsList.value = Status.Loading()

        val result = contactDomain.getContactsList(forceUpdate)
        if (result is Resource.Success) {
            val userContacts = mutableListOf<UserModel>()
            val contactsList = result.data


            contactsList.map { contact ->


                //obtener datos de usuario

                val userRequest =
                    userRepository.getUserData(contact.contact.userAsContact, false)
                if (userRequest is Resource.Success) {
                    userContacts.add(
                        userRequest.data
                    )

                }
            }

            if (userContacts.isNotEmpty())
                _contactsList.value = Status.Success(userContacts)
            else _contactsList.value = Status.Error("No se obtuvieron resultados.")

        } else _contactsList.value = Status.Error(result.message ?: "")


    }

    suspend fun searchUser(query: String) {
        if (query.trim().isNotEmpty()) {

            //realizar busqueda
            val result = userRepository.searchUsers(query)
            if (result is Resource.Success) {

                val contacts = result.data.first
                val externalUsers = result.data.second

                _contactsList.value =
                    if (contacts != null) Status.Success(contacts) else Status.Error("No results")

                _externalUsersList.value =
                    if (externalUsers != null) Status.Success(externalUsers) else Status.Error("No results")

            } else {
                _contactsList.value = Status.Error("No results")
                _externalUsersList.value = Status.Error("No results")

            }
        }
    }

    fun clearExternalUsers(){
        _externalUsersList.value = Status.Error("No results")
    }


}