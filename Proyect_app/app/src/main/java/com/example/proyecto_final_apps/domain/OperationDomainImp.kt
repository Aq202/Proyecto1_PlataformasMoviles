package com.example.proyecto_final_apps.domain

import com.example.proyecto_final_apps.data.Resource
import com.example.proyecto_final_apps.data.local.entity.OperationModel
import com.example.proyecto_final_apps.data.repository.AccountRepository
import com.example.proyecto_final_apps.data.repository.ContactRepository
import com.example.proyecto_final_apps.data.repository.DebtRepository
import com.example.proyecto_final_apps.data.repository.OperationRepository
import javax.inject.Inject

class OperationDomainImp @Inject constructor(
    private val accountRepository: AccountRepository,
    private val operationRepository: OperationRepository,
    private val contactRepository: ContactRepository,
    private val debtRepository: DebtRepository
) : OperationDomain {

    /*
        Se encarga de manejar la actualización remota de las cuentas previo a actualizar cuentas
     */
    override suspend fun getOperations(forceUpdate: Boolean): Resource<List<OperationModel>> {

        //Download account data
        if (forceUpdate) {
            accountRepository.getAccountList(true)
            contactRepository.getContactsList(true)
            debtRepository.getDebtList(true)
        }

        return operationRepository.getOperations(forceUpdate)

    }

    /*
        Se encarga de manejar la actualización remota de las cuentas previo a actualizar cuentas
     */
    override suspend fun getOperationData(
        operationLocalId: Int,
        forceUpdate: Boolean
    ): Resource<OperationModel> {

        //Download account data
        if (forceUpdate) {
            accountRepository.getAccountList(true)
            contactRepository.getContactsList(true)
            debtRepository.getDebtList(true)
        }

        return operationRepository.getOperationData(operationLocalId, forceUpdate)
    }

    override suspend fun removeFavoriteOperation(operationLocalId: Int):
            Resource<OperationModel> {

        return operationRepository.updateOperation(
            operationLocalId,
            favorite = false,  //Elimnar de favoritos
            accountLocalId = null,
            active = null,
            amount = null,
            category = null,
            description = null,
            imgUrl = null,
            title = null
        )
    }
}