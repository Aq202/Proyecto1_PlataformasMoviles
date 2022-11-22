package com.example.proyecto_final_apps.domain

import com.example.proyecto_final_apps.data.Resource
import com.example.proyecto_final_apps.data.local.entity.AccountModel
import com.example.proyecto_final_apps.data.local.entity.OperationModel

interface AccountDomain {

    suspend fun createAccount(
        title: String,
        total: Double,
        defaultAccount: Boolean
    ): Resource<AccountModel>

    suspend fun updateAccount(
        accountLocalId: Int, title: String?, defaultAccount: Boolean?, total: Double
    ):Resource<AccountModel>

    suspend fun getAccountList(forceUpdate:Boolean):Resource<List<AccountModel>>

    suspend fun getAccountData(accountLocalId:Int, forceUpdate: Boolean):Resource<AccountModel>

    suspend fun makeAccountTransaction(originAccountLocalId:Int, targetAccountLocalId:Int, amount:Double, description:String?):Resource<OperationModel>
}