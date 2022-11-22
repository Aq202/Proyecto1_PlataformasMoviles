package com.example.proyecto_final_apps.domain

import android.content.Context
import com.example.proyecto_final_apps.R
import com.example.proyecto_final_apps.data.Category
import com.example.proyecto_final_apps.data.CategoryTypes
import com.example.proyecto_final_apps.data.Resource
import com.example.proyecto_final_apps.data.local.entity.AccountModel
import com.example.proyecto_final_apps.data.local.entity.OperationModel
import com.example.proyecto_final_apps.data.repository.AccountRepository
import com.example.proyecto_final_apps.data.repository.ContactRepository
import com.example.proyecto_final_apps.data.repository.DebtRepository
import com.example.proyecto_final_apps.data.repository.OperationRepository
import com.example.proyecto_final_apps.helpers.DateParse
import javax.inject.Inject

class AccountDomainImp @Inject constructor(
    private val context: Context,
    private val accountRepository: AccountRepository,
    private val operationRepository: OperationRepository,
    private val contactRepository: ContactRepository,
    private val debtRepository: DebtRepository
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

        if (result is Resource.Success) {
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

    override suspend fun updateAccount(
        accountLocalId: Int,
        title: String?,
        defaultAccount: Boolean?,
        total: Double
    ): Resource<AccountModel> {

        //Actualizar cuenta
        val result = accountRepository.updateAccount(accountLocalId, title, defaultAccount)

        if (result is Resource.Success) {

            //Obtener balance de cuenta
            val accountResult =
                accountRepository.getAccountData(accountLocalId, true) //Forzar actualización

            if (accountResult is Resource.Success) {
                val account = accountResult.data
                //crear operación para ajustar total
                if (total != account.total) {
                    val difference =
                        if (total > account.total) total - account.total else account.total - total

                    operationRepository.createOperation(
                        title = context.getString(R.string.account_update_operation_title),
                        accountLocalId = accountLocalId,
                        amount = difference,
                        active = total > account.total,
                        category = Category(context).getCategoryByType(CategoryTypes.DEFAULT)!!.id,
                        date = DateParse.getCurrentDate(),
                        description = null,
                        favorite = false
                    )
                }


            } else
                return Resource.Error("No se pudo actualizar el total de la cuenta.")

        }

        return result
    }

    override suspend fun getAccountList(forceUpdate: Boolean): Resource<List<AccountModel>> {
        val result = accountRepository.getAccountList(forceUpdate)

        if (forceUpdate) {
            operationRepository.getOperations(true)
            contactRepository.getContactsList(true)
            debtRepository.getDebtList(true)
        }

        return result
    }

    override suspend fun getAccountData(
        accountLocalId: Int,
        forceUpdate: Boolean
    ): Resource<AccountModel> {

        val result = accountRepository.getAccountData(accountLocalId, forceUpdate)

        if (forceUpdate) {
            operationRepository.getOperations(true)
            contactRepository.getContactsList(true)
            debtRepository.getDebtList(true)
        }
        return result
    }

    override suspend fun makeAccountTransaction(
        originAccountLocalId: Int,
        targetAccountLocalId: Int,
        amount: Double,
        description: String?
    ): Resource<OperationModel> {

        //obtener data de cuentas
        val originAccountResult = accountRepository.getAccountData(originAccountLocalId, false)
        val originAccount =
            if (originAccountResult is Resource.Success) originAccountResult.data else return Resource.Error(
                originAccountResult.message ?: "Error al obtener cuenta de origen."
            )

        val targetAccountResult = accountRepository.getAccountData(targetAccountLocalId, false)
        val targetAccount =
            if (targetAccountResult is Resource.Success) targetAccountResult.data else return Resource.Error(
                targetAccountResult.message ?: "Error al obtener cuenta de origen."
            )

        val defaultDescription = context.getString(R.string.transfer_description)
        val category = Category(context).getCategoryByType(CategoryTypes.DEFAULT)!!.id

        //Realizar retiro de fondos
        val originAccountOperation = operationRepository.createOperation(
            title = context.getString(R.string.origin_transfer_title, targetAccount.title),
            accountLocalId = originAccountLocalId,
            amount = amount,
            active = false,
            description = description ?: defaultDescription,
            category = category,
            favorite = false,
            date = DateParse.getCurrentDate()
        )

        if (originAccountOperation is Resource.Error) return Resource.Error("Error al retirar fondos: ${originAccountOperation.message ?: "Ocurrió un error."}")

        return operationRepository.createOperation(
            title = context.getString(R.string.target_transfer_title, originAccount.title),
            accountLocalId = targetAccountLocalId,
            amount = amount,
            active = true,
            description = description ?: defaultDescription,
            category = category,
            favorite = false,
            date = DateParse.getCurrentDate()
        )
    }


}