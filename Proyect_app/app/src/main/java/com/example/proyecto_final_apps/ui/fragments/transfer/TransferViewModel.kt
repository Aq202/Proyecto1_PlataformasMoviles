package com.example.proyecto_final_apps.ui.fragments.transfer

import androidx.lifecycle.ViewModel
import com.example.proyecto_final_apps.data.Resource
import com.example.proyecto_final_apps.data.local.entity.AccountModel
import com.example.proyecto_final_apps.data.local.entity.OperationModel
import com.example.proyecto_final_apps.domain.AccountDomain
import com.example.proyecto_final_apps.ui.util.Status
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

@HiltViewModel
class TransferViewModel @Inject constructor(private val accountDomain: AccountDomain )  : ViewModel(){

    private val _accountsList = MutableStateFlow<Status<List<AccountModel>>>(Status.Default())
    val accountsList:StateFlow<Status<List<AccountModel>>> = _accountsList

    suspend fun getAccountsList(forceUpdate:Boolean){

        _accountsList.value = Status.Loading()

        val result = accountDomain.getAccountList(forceUpdate)
        if(result is Resource.Success) _accountsList.value = Status.Success(result.data.filter { it.editable })
        else _accountsList.value = Status.Error(result.message ?: "No se obtuvo ninguna cuenta.")

    }

    suspend fun makeAccountsTransfer(originAccountId:Int, targetAccountId:Int, amount:Double, description:String?) :Flow<Status<OperationModel>>{
        return flow{

            emit(Status.Loading())

            val result = accountDomain.makeAccountTransaction(originAccountId, targetAccountId, amount, description)

            if(result is Resource.Success) emit(Status.Success(result.data))
            else emit(Status.Error(result.message ?: "Ocurrió un error al realizar la transferencia."))
        }
    }

}