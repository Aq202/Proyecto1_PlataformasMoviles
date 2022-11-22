package com.example.proyecto_final_apps.data.repository

import com.example.proyecto_final_apps.data.Resource
import com.example.proyecto_final_apps.data.local.entity.DebtAcceptedModel
import com.example.proyecto_final_apps.data.local.entity.DebtWithContactModel
import com.example.proyecto_final_apps.data.local.entity.OperationModel
import com.example.proyecto_final_apps.data.local.entity.UserModel

interface DebtRepository {
    suspend fun createDebt(
        contactUserId: String,
        active: Boolean,
        amount: Double,
        accountLocalId: Int,
        description: String?,
    ): Resource<DebtAcceptedModel>

    suspend fun finalizeDebt(
        debtLocalID:Int,
        updateRemotely:Boolean = true
    ):Resource<Boolean>

    suspend fun getAcceptedDebtData(
        acceptedDebtLocalId:Int
    ):Resource<DebtWithContactModel>

    suspend fun getDebtList(
        forceUpdate:Boolean
    ):Resource<List<Pair<DebtAcceptedModel, UserModel>>>

    suspend fun completeDebt(debtLocalId:Int, targetAccountId:Int):Resource<OperationModel>
}