package com.example.proyecto_final_apps.domain

import com.example.proyecto_final_apps.data.Resource
import com.example.proyecto_final_apps.data.local.entity.DebtAcceptedModel
import com.example.proyecto_final_apps.data.local.entity.DebtWithContactModel
import com.example.proyecto_final_apps.data.repository.AccountRepository
import com.example.proyecto_final_apps.data.repository.ContactRepository
import com.example.proyecto_final_apps.data.repository.DebtRepository
import com.example.proyecto_final_apps.data.repository.OperationRepository
import javax.inject.Inject


class DebtDomainImp @Inject constructor(
    private val accountRepository: AccountRepository,
    private val operationRepository: OperationRepository,
    private val contactRepository: ContactRepository,
    private val debtRepository: DebtRepository
) : DebtDomain {

    override suspend fun getAcceptedDebtData(
        acceptedDebtLocalId: Int,
        forceUpdate: Boolean
    ): Resource<DebtWithContactModel> {

        if (forceUpdate) {
            accountRepository.getAccountList(true)
            operationRepository.getOperations(true)
            contactRepository.getContactsList(true)
            debtRepository.getDebtList(true)
        }

        return debtRepository.getAcceptedDebtData(acceptedDebtLocalId)
    }

    override suspend fun finalizeDebt(
        debtLocalID: Int,
    ): Resource<Boolean> {

        return debtRepository.finalizeDebt(debtLocalID)
    }

    override suspend fun getDebtList(forceUpdate: Boolean): Resource<List<DebtAcceptedModel>> {
        if (forceUpdate) {
            accountRepository.getAccountList(true)
            operationRepository.getOperations(true)
            contactRepository.getContactsList(true)
        }
        return debtRepository.getDebtList(forceUpdate)

    }

}