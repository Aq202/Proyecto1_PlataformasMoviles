package com.example.proyecto_final_apps.ui.fragments.newAccount

import androidx.lifecycle.ViewModel
import com.example.proyecto_final_apps.data.Resource
import com.example.proyecto_final_apps.data.local.entity.AccountModel
import com.example.proyecto_final_apps.data.repository.AccountRepository
import com.example.proyecto_final_apps.ui.util.Status
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

@HiltViewModel
class NewAccountViewModel @Inject constructor(private val acRepository: AccountRepository) :
    ViewModel() {


    fun createAccount(
        title: String,
        total: Double,
        defaultAccount: Boolean
    ): Flow<Status<AccountModel>> {

        return flow {

            emit(Status.Loading())

            val result = acRepository.createAccount(title=title, total = total, defaultAccount=defaultAccount)

            if(result is Resource.Success)
                emit(Status.Success(result.data))
            else emit(Status.Error(result.message ?: ""))
        }
    }

}