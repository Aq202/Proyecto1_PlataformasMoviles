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
import com.example.proyecto_final_apps.ui.util.ErrorType
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

    enum class NewDebtErrors : ErrorType {
        NO_CONTACT,
        NO_ACCOUNTS
    }

    private val _fragmentState = MutableStateFlow<Status<Boolean>>(Status.Loading())
    val fragmentState: StateFlow<Status<Boolean>> = _fragmentState

    private val _contactsList =
        MutableStateFlow<Status<List<ContactWithUserModel>>>(Status.Loading())
    val contactsList: StateFlow<Status<List<ContactWithUserModel>>> = _contactsList

    private val _accountsList = MutableStateFlow<Status<List<AccountModel>>>(Status.Loading())
    val accountsList: StateFlow<Status<List<AccountModel>>> = _accountsList


    suspend fun getFormData(forceUpdate:Boolean = false) {

            //get contacts
            val contactResult = contactDomain.getContactsList(forceUpdate)
            if (contactResult is Resource.Success) _contactsList.value =
                Status.Success(contactResult.data)
            else {
                _fragmentState.value =
                    Status.Error(contactResult.message ?: "", NewDebtErrors.NO_CONTACT)
                return
            }

            val accountResult = accountDomain.getAccountList(false) //Ya se actualizó al obtener contactos
            if (accountResult is Resource.Success) {
                val editableAccount = accountResult.data.filter { it.editable }
                if (editableAccount.isNotEmpty()) {
                    _accountsList.value = Status.Success(editableAccount)
                } else {
                    //No se encontraron cuentas editables
                    _fragmentState.value = Status.Error(
                        "No se encontraron cuentas disponibles.",
                        NewDebtErrors.NO_ACCOUNTS)
                    return
                }

            } else {
                _fragmentState.value = Status.Error(accountResult.message ?: "")
                return
            }

            _fragmentState.value = Status.Success(true)

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