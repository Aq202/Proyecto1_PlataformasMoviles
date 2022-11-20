package com.example.proyecto_final_apps.ui.fragments.new_debt

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyecto_final_apps.data.Resource
import com.example.proyecto_final_apps.data.local.entity.AccountModel
import com.example.proyecto_final_apps.data.local.entity.ContactModel
import com.example.proyecto_final_apps.data.local.entity.ContactWithUserModel
import com.example.proyecto_final_apps.data.local.entity.DebtAcceptedModel
import com.example.proyecto_final_apps.data.repository.AccountRepository
import com.example.proyecto_final_apps.data.repository.ContactRepository
import com.example.proyecto_final_apps.data.repository.DebtRepository
import com.example.proyecto_final_apps.domain.AccountDomain
import com.example.proyecto_final_apps.domain.ContactDomain
import com.example.proyecto_final_apps.ui.util.Status
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewDebtViewModel @Inject constructor(
    private val debtRepository: DebtRepository,
    private val accountDomain: AccountDomain,
    private val contactDomain: ContactDomain
) : ViewModel() {

    private val _fragmentState = MutableStateFlow<Status<Boolean>>(Status.Loading())
    val fragmentState: StateFlow<Status<Boolean>> = _fragmentState

    private val _contactsList =
        MutableStateFlow<Status<List<ContactWithUserModel>>>(Status.Loading())
    val contactsList: StateFlow<Status<List<ContactWithUserModel>>> = _contactsList

    private val _accountsList = MutableStateFlow<Status<List<AccountModel>>>(Status.Loading())
    val accountsList: StateFlow<Status<List<AccountModel>>> = _accountsList


    fun getFormData() {
        viewModelScope.launch {
            //get contacts
            val contactResult = contactDomain.getContactsList(true)
            if (contactResult is Resource.Success) _contactsList.value =
                Status.Success(contactResult.data)
            else _fragmentState.value = Status.Error(contactResult.message ?: "")

            val accountResult = accountDomain.getAccountList(true)
            if (accountResult is Resource.Success) _accountsList.value =
                Status.Success(accountResult.data)
            else _fragmentState.value = Status.Error(accountResult.message ?: "")

            if (contactResult is Resource.Success && accountResult is Resource.Success) _fragmentState.value =
                Status.Success(true)
        }
    }

    fun createNewDebt(
        contact: ContactModel,
        active: Boolean,
        amount: Double,
        account: AccountModel,
        description: String?,
    ): Flow<Status<DebtAcceptedModel>> {

        return flow {

            emit(Status.Loading())

            val result = debtRepository.createDebt(
                contactUserId = contact.userAsContact,
                active = active,
                amount = amount,
                accountLocalId = account.localId!!,
                description = description,
            )

            if (result is Resource.Success) emit(Status.Success(result.data))
            else emit(Status.Error(result.message ?: ""))

        }

    }
}