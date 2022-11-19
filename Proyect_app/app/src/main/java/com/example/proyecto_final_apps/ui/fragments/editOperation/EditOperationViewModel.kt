package com.example.proyecto_final_apps.ui.fragments.editOperation

import androidx.lifecycle.ViewModel
import com.example.proyecto_final_apps.data.Resource
import com.example.proyecto_final_apps.data.local.entity.AccountModel
import com.example.proyecto_final_apps.data.local.entity.OperationModel
import com.example.proyecto_final_apps.data.repository.AccountRepository
import com.example.proyecto_final_apps.data.repository.OperationRepository
import com.example.proyecto_final_apps.domain.OperationDomain
import com.example.proyecto_final_apps.ui.util.Status
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class EditOperationViewModel @Inject constructor(
    private val opRepository: OperationRepository,
    private val acRepository: AccountRepository,
    private val opDomain:OperationDomain
    ) :
    ViewModel() {

    private val _operationData: MutableStateFlow<Status<OperationModel>> =
        MutableStateFlow(Status.Default())
    val operationData: StateFlow<Status<OperationModel>> = _operationData

    private val _accountData: MutableStateFlow<Status<AccountModel>> =
        MutableStateFlow(Status.Default())
    val accountData: StateFlow<Status<AccountModel>> = _accountData

    suspend fun getOperationData(localOperationId: Int, forceUpdate: Boolean) {
        val result = opDomain.getOperationData(localOperationId, forceUpdate)

        if (result is Resource.Success)
            _operationData.value = Status.Success(result.data)
        else
            _operationData.value = Status.Error(result.message ?: "")

    }

    suspend fun getAccountData(localOperationId: Int, forceUpdate: Boolean) {
        var accountModel: Resource<AccountModel>
        val operation = opDomain.getOperationData(localOperationId, forceUpdate)

        if (operation is Resource.Success){
            accountModel = acRepository.getAccountData(operation.data.accountLocalId, forceUpdate)
            if(accountModel is Resource.Success)
                _accountData.value = Status.Success(accountModel.data)
            else
                _accountData.value = Status.Error(operation.message ?: "")
        }
        else
            _accountData.value = Status.Error(operation.message ?: "")

    }
}