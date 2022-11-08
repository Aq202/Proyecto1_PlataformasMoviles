package com.example.proyecto_final_apps.data.repository

import com.example.proyecto_final_apps.data.Resource
import com.example.proyecto_final_apps.data.local.entity.AccountModel

interface AccountRepository {

    suspend fun getAccountList(forceUpdate:Boolean):Resource<List<AccountModel>>
    suspend fun getAccountBalance(accountLocalId:Int):Resource<Double>
    suspend fun getAccountBalanceMovement(accountLocalId:Int):Resource<Double>
    suspend fun getAccountName(accountLocalId:Int, forceUpdate: Boolean):Resource<String>

}