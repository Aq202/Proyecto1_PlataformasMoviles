package com.example.proyecto_final_apps.data.remote.dto

import com.example.proyecto_final_apps.data.local.entity.DebtAcceptedModel

data class DebtsAcceptedDto(
    val accountInvolved: String,
    val active: Boolean,
    val amount: Double,
    val id: String,
    val localId: Int,
    val subject: String,
    val userInvolved: String
)

fun DebtsAcceptedDto.toDebtAcceptedModel(): DebtAcceptedModel {
    return DebtAcceptedModel(
        localId = localId,
        accountInvolved = accountInvolved,
        active = active,
        amount = amount,
        remoteId = id,
        userInvolved = userInvolved
    )
}