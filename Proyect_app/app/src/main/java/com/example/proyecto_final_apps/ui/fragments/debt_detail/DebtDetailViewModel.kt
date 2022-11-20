package com.example.proyecto_final_apps.ui.fragments.debt_detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyecto_final_apps.data.Resource
import com.example.proyecto_final_apps.data.local.entity.AccountModel
import com.example.proyecto_final_apps.data.local.entity.DebtWithContactModel
import com.example.proyecto_final_apps.domain.AccountDomain
import com.example.proyecto_final_apps.domain.DebtDomain
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
class DebtDetailViewModel @Inject constructor(
    private val accountDomain: AccountDomain,
    private val debtDomain: DebtDomain
) : ViewModel() {


    private val _fragmentState = MutableStateFlow<Status<Boolean>>(Status.Loading())
    val fragmentState: StateFlow<Status<Boolean>> = _fragmentState

    private val _accountList = MutableStateFlow<Status<List<AccountModel>>>(Status.Loading())
    val accountList: StateFlow<Status<List<AccountModel>>> = _accountList

    private val _debtData = MutableStateFlow<Status<DebtWithContactModel>>(Status.Loading())
    val debtData: StateFlow<Status<DebtWithContactModel>> = _debtData


    suspend fun getFragmentData(debtAcceptedLocalId: Int, forceUpdate: Boolean = false) {

            val accountListResult = accountDomain.getAccountList(forceUpdate)
            val debtDataResult = debtDomain.getAcceptedDebtData(
                debtAcceptedLocalId,
                false
            ) //Ya se actualizó con la llamada a cuenta

            if (debtDataResult is Resource.Success) {
                _debtData.value = Status.Success(debtDataResult.data)
                _fragmentState.value = Status.Success(true)
            } else _fragmentState.value =
                Status.Error(debtDataResult.message ?: "Ocurrió un error.")

            if (accountListResult is Resource.Success) {
                val editableAccounts = accountListResult.data.filter { it.editable }
                if (editableAccounts.isNotEmpty())
                    _accountList.value = Status.Success(editableAccounts)
                else
                    _accountList.value = Status.Error(accountListResult.message ?: "Ocurrió un error al obtener cuentas.")
            }


    }

    fun finalizeDebt(debtLocalId:Int):Flow <Status<Boolean>>{
        return flow{

            emit(Status.Loading())

            val result = debtDomain.finalizeDebt(debtLocalId)

            if(result is Resource.Success) emit(Status.Success(true))
            else emit(Status.Error(result.message ?: "Ocurrió un error."))
        }
    }
}