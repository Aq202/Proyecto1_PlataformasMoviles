package com.example.proyecto_final_apps.ui.fragments.editAccount

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyecto_final_apps.data.Resource
import com.example.proyecto_final_apps.data.local.entity.AccountModel
import com.example.proyecto_final_apps.data.repository.AccountRepository
import com.example.proyecto_final_apps.domain.AccountDomain
import com.example.proyecto_final_apps.ui.util.Status
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditAccountViewModel @Inject constructor(
    private val accountDomain: AccountDomain,
) :
    ViewModel() {

    private val _accountData: MutableStateFlow<Status<AccountModel>> =
        MutableStateFlow(Status.Default())
    val accountData: StateFlow<Status<AccountModel>> = _accountData


    fun updateAccount(
        accountLocalId: Int,
        title: String,
        total: Double,
    ): Flow<Status<AccountModel>> {

        return flow {

            emit(Status.Loading())

            val result = accountDomain.updateAccount(
                accountLocalId = accountLocalId,
                title = title,
                total = total,
                defaultAccount = null
            )

            if (result is Resource.Success)
                emit(Status.Success(result.data))
            else emit(Status.Error(result.message ?: ""))
        }
    }

    fun getAccountData(localAccountId: Int) {

        viewModelScope.launch {

            _accountData.value = Status.Loading()

            val result = accountDomain.getAccountData(localAccountId, false)

            if (result is Resource.Success)
                _accountData.value = Status.Success(result.data)
            else
                _accountData.value = Status.Error(result.message ?: "")
        }

    }

}