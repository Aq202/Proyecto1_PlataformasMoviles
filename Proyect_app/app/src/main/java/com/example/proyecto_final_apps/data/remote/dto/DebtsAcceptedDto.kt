package com.example.proyecto_final_apps.data.remote.dto

import com.example.proyecto_final_apps.data.local.entity.DebtAcceptedModel

data class DebtsAcceptedDto(
    val accountInvolved: AccountDto,
    val active: Boolean,
    val amount: Double,
    val id: String,
    val localId: Int,
    val subject: String,
    val userInvolved: String,
    val description:String?
)

fun DebtsAcceptedDto.toDebtAcceptedModel(): DebtAcceptedModel {
    return DebtAcceptedModel(
        localId = localId,
        accountInvolved = accountInvolved.localId,
        active = active,
        amount = amount,
        remoteId = id,
        userInvolved = userInvolved,
        description = description
    )
}