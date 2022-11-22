package com.example.proyecto_final_apps.domain

import com.example.proyecto_final_apps.data.Resource
import com.example.proyecto_final_apps.data.local.entity.DebtAcceptedModel
import com.example.proyecto_final_apps.data.local.entity.DebtWithContactModel
import com.example.proyecto_final_apps.data.local.entity.OperationModel
import com.example.proyecto_final_apps.data.local.entity.UserModel

interface DebtDomain {

    suspend fun getAcceptedDebtData(
        acceptedDebtLocalId: Int,
        forceUpdate: Boolean
    ): Resource<DebtWithContactModel>

    suspend fun finalizeDebt(
        debtLocalID: Int,
    ): Resource<Boolean>

    suspend fun getDebtList(forceUpdate: Boolean): Resource<List<Pair<DebtAcceptedModel, UserModel>>>

    suspend fun completeDebt(debtLocalId:Int, targetAccountId:Int):Resource<OperationModel>
}