package com.example.proyecto_final_apps.ui.fragments.tabLayout

import androidx.lifecycle.ViewModel
import com.example.proyecto_final_apps.data.Resource
import com.example.proyecto_final_apps.data.local.entity.AccountModel
import com.example.proyecto_final_apps.data.local.entity.OperationModel
import com.example.proyecto_final_apps.domain.AccountDomain
import com.example.proyecto_final_apps.domain.OperationDomain
import com.example.proyecto_final_apps.ui.util.Status
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class TabLayoutViewModel @Inject constructor(
    private val opDomain: OperationDomain,
    private val acDomain: AccountDomain
) : ViewModel() {

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
            accountModel = acDomain.getAccountData(operation.data.accountLocalId, forceUpdate)
            if(accountModel is Resource.Success)
                _accountData.value = Status.Success(accountModel.data)
            else
                _accountData.value = Status.Error(operation.message ?: "")
        }
        else
            _accountData.value = Status.Error(operation.message ?: "")

    }

    fun deleteData(){
        _operationData.value = Status.Default()
        _accountData.value = Status.Default()
    }

}