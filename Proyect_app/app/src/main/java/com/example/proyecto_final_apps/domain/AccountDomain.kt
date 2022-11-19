package com.example.proyecto_final_apps.domain

import com.example.proyecto_final_apps.data.Resource
import com.example.proyecto_final_apps.data.local.entity.AccountModel

interface AccountDomain {

    suspend fun createAccount(
        title: String,
        total: Double,
        defaultAccount: Boolean
    ): Resource<AccountModel>

    suspend fun updateAccount(
        accountLocalId: Int, title: String?, defaultAccount: Boolean?, total: Double
    ):Resource<AccountModel>
}