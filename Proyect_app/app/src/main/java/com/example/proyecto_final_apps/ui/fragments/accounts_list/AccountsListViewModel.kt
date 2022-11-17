package com.example.proyecto_final_apps.ui.fragments.accounts_list

import androidx.lifecycle.ViewModel
import com.example.proyecto_final_apps.data.Resource
import com.example.proyecto_final_apps.data.local.entity.AccountModel
import com.example.proyecto_final_apps.data.repository.AccountRepository
import com.example.proyecto_final_apps.data.repository.OperationRepository
import com.example.proyecto_final_apps.ui.util.Status
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class AccountsListViewModel @Inject constructor(
    private val acRepository: AccountRepository,
    private val opRepository: OperationRepository
) : ViewModel() {

    private val _fragmentState: MutableStateFlow<Status<Boolean>> =
        MutableStateFlow(Status.Loading())
    val fragmentState: StateFlow<Status<Boolean>> = _fragmentState

    private val _accountList: MutableStateFlow<Status<List<AccountModel>>> =
        MutableStateFlow(Status.Default())
    val accountList: StateFlow<Status<List<AccountModel>>> = _accountList

    private val _balanceData: MutableStateFlow<Status<Pair<Double, Double>>> =
        MutableStateFlow(Status.Default())
    val balanceData: StateFlow<Status<Pair<Double, Double>>> = _balanceData


    suspend fun getAccountList(forceUpdate: Boolean = false) {

        when (val accountList = acRepository.getAccountList(forceUpdate)) {
            is Resource.Success -> {
                _accountList.value = Status.Success(accountList.data)

            }
            else -> {
                _accountList.value = Status.Error(accountList.message ?: "")

            }
        }
    }

    suspend fun getGeneralDescription() {

        val generalBalanceResult = opRepository.getGeneralBalance()
        val balanceMovementResult = opRepository.getBalanceMovement()

        if (generalBalanceResult is Resource.Success && balanceMovementResult is Resource.Success) {

            _balanceData.value =
                Status.Success(Pair(generalBalanceResult.data, balanceMovementResult.data))
            _fragmentState.value = Status.Success(true)
        } else if (generalBalanceResult is Resource.Success) {
            _balanceData.value = Status.Error(balanceMovementResult.message ?: "")
            _fragmentState.value = Status.Error("")
        } else {
            _balanceData.value = Status.Error(generalBalanceResult.message ?: "")
            _fragmentState.value = Status.Error("")
        }

    }

}