package com.example.proyecto_final_apps.data.repository

import com.example.proyecto_final_apps.data.Resource
import com.example.proyecto_final_apps.data.local.entity.DebtAcceptedModel
import com.example.proyecto_final_apps.data.local.entity.DebtWithContactModel

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
}