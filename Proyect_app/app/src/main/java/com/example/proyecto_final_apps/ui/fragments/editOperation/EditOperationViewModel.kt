package com.example.proyecto_final_apps.ui.fragments.editOperation

import androidx.lifecycle.ViewModel
import com.example.proyecto_final_apps.data.Resource
import com.example.proyecto_final_apps.data.local.entity.AccountModel
import com.example.proyecto_final_apps.data.local.entity.OperationModel
import com.example.proyecto_final_apps.data.repository.AccountRepository
import com.example.proyecto_final_apps.data.repository.OperationRepository
import com.example.proyecto_final_apps.domain.AccountDomain
import com.example.proyecto_final_apps.domain.OperationDomain
import com.example.proyecto_final_apps.helpers.DateParse.getDayValue
import com.example.proyecto_final_apps.helpers.DateParse.getMonthValue
import com.example.proyecto_final_apps.helpers.DateParse.getYearValue
import com.example.proyecto_final_apps.ui.util.Status
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import java.util.*
import javax.inject.Inject

@HiltViewModel
class EditOperationViewModel @Inject constructor(
    private val opDomain:OperationDomain,
    private val opRepository: OperationRepository,
    private val accountDomain:AccountDomain
    ) :
    ViewModel() {

    private val _fragmentState:MutableStateFlow<Status<Boolean>> = MutableStateFlow(Status.Loading())
    val fragmentState:StateFlow<Status<Boolean>> = _fragmentState

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
            accountModel = accountDomain.getAccountData(operation.data.accountLocalId, forceUpdate)
            if(accountModel is Resource.Success)
                _accountData.value = Status.Success(accountModel.data)
            else
                _accountData.value = Status.Error(operation.message ?: "")
        }
        else
            _accountData.value = Status.Error(operation.message ?: "")

    }

    fun updateOperation(
        operationLocalId: Int,
        title: String,
        accountLocalId: Int,
        amount: Double,
        active: Boolean,
        description: String?,
        category: Int,
        favorite: Boolean,
        imgUrl: String?,
    ): Flow<Status<OperationModel>> {

        return flow {

            emit(Status.Loading())

            val result = opRepository.updateOperation(
                operationLocalId = operationLocalId,
                title = title,
                accountLocalId = accountLocalId,
                amount = amount,
                active = active,
                description = description,
                category = category,
                favorite = favorite,
                imgUrl = imgUrl
            )

            if(result is Resource.Success)
                emit(Status.Success(result.data))
            else emit(Status.Error(result.message ?: ""))
        }
    }

    fun setSuccessFragmentStatus(){
        _fragmentState.value = Status.Success(true)
    }
}