package com.example.proyecto_final_apps.data.remote.dto

import com.example.proyecto_final_apps.data.local.entity.AccountModel

data class AccountDto(
    val allowNegativeValues: Boolean,
    val defaultAccount: Boolean,
    val editable: Boolean,
    val id: String,
    val localId: Int,
    val subject: String,
    val title: String,
)

fun AccountDto.toAccountModel():AccountModel{
    return AccountModel(
        localId = localId,
        remoteId = id,
        allowNegativeValues = allowNegativeValues,
        defaultAccount = defaultAccount,
        editable = editable,
        subject = subject,
        title = title,
    )
}