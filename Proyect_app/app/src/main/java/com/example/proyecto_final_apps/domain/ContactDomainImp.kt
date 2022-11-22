package com.example.proyecto_final_apps.domain

import com.example.proyecto_final_apps.data.Resource
import com.example.proyecto_final_apps.data.local.entity.ContactFullDataModel
import com.example.proyecto_final_apps.data.local.entity.ContactWithUserModel
import com.example.proyecto_final_apps.data.repository.AccountRepository
import com.example.proyecto_final_apps.data.repository.ContactRepository
import com.example.proyecto_final_apps.data.repository.DebtRepository
import com.example.proyecto_final_apps.data.repository.OperationRepository
import javax.inject.Inject

class ContactDomainImp @Inject constructor(
    private val accountRepository: AccountRepository,
    private val  contactRepository: ContactRepository,
    private val operationRepository: OperationRepository,
    private val debtRepository: DebtRepository
) : ContactDomain {
    override suspend fun getContactsList(forceUpdate: Boolean): Resource<List<ContactWithUserModel>> {

        //Realizar actualización de datos
        if(forceUpdate){
            accountRepository.getAccountList(true)
            operationRepository.getOperations(true)
            debtRepository.getDebtList(true)

        }

        return contactRepository.getContactsList(forceUpdate)

    }

    override suspend fun getContactData(
        userId: String,
        forceUpdate: Boolean
    ): Resource<ContactFullDataModel> {
        //Realizar actualización de datos
        if(forceUpdate){
            accountRepository.getAccountList(true)
            operationRepository.getOperations(true)
            debtRepository.getDebtList(true)
        }

        return contactRepository.getContactData(userId, forceUpdate)
    }
}