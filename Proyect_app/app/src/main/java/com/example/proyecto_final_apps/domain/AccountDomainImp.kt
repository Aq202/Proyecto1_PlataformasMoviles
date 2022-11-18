package com.example.proyecto_final_apps.domain

import android.content.Context
import com.example.proyecto_final_apps.R
import com.example.proyecto_final_apps.data.Category
import com.example.proyecto_final_apps.data.CategoryTypes
import com.example.proyecto_final_apps.data.Resource
import com.example.proyecto_final_apps.data.local.entity.AccountModel
import com.example.proyecto_final_apps.data.repository.AccountRepository
import com.example.proyecto_final_apps.data.repository.OperationRepository
import com.example.proyecto_final_apps.helpers.DateParse
import javax.inject.Inject

class AccountDomainImp @Inject constructor(
    private val context: Context,
    private val accountRepository: AccountRepository,
    private val operationRepository: OperationRepository
) : AccountDomain {

    override suspend fun createAccount(
        title: String,
        total: Double,
        defaultAccount: Boolean
    ): Resource<AccountModel> {

        //crear cuenta
        val result = accountRepository.createAccount(
            title = title,
            defaultAccount = defaultAccount
        )

        if(result is Resource.Success){
            val account = result.data

            //crear operación inicial
            operationRepository.createOperation(
                title = context.getString(
                    R.string.titulo_operacion_inicial_cuenta,
                    title.trim()
                ),
                accountLocalId = account.localId!!,
                amount = total,
                active = true,
                description = context.getString(R.string.descripcion_operacion_inicial_cuenta),
                category = Category(context).getCategoryByType(CategoryTypes.DEFAULT)!!.id,
                favorite = false,
                date = DateParse.getCurrentDate(),
            )
        }

        return result
    }
}