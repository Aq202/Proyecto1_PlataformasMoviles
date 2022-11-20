package com.example.proyecto_final_apps.domain

import com.example.proyecto_final_apps.data.Resource
import com.example.proyecto_final_apps.data.local.entity.OperationModel
import com.example.proyecto_final_apps.data.repository.AccountRepository
import com.example.proyecto_final_apps.data.repository.ContactRepository
import com.example.proyecto_final_apps.data.repository.OperationRepository
import javax.inject.Inject

class OperationDomainImp @Inject constructor(
    private val accountRepository: AccountRepository,
    private val operationRepository: OperationRepository,
    private val contactRepository: ContactRepository
) : OperationDomain {

    /*
        Se encarga de manejar la actualización remota de las cuentas previo a actualizar cuentas
     */
    override suspend fun getOperations(forceUpdate: Boolean): Resource<List<OperationModel>> {

        //Download account data
        if(forceUpdate){
            accountRepository.getAccountList(true)
            contactRepository.getContactsList(true)
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
        if(forceUpdate) {
            accountRepository.getAccountList(true)
            contactRepository.getContactsList(true)
        }

        return operationRepository.getOperationData(operationLocalId, forceUpdate)
    }
}