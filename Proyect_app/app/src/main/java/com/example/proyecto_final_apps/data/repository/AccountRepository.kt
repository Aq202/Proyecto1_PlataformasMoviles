package com.example.proyecto_final_apps.data.repository

import com.example.proyecto_final_apps.data.Resource
import com.example.proyecto_final_apps.data.local.entity.AccountModel

interface AccountRepository {

    suspend fun getAccountList(forceUpdate:Boolean):Resource<List<AccountModel>>
    suspend fun getAccountBalance(accountLocalId:Int):Resource<Double>
    suspend fun getAccountBalanceMovement(accountLocalId:Int):Resource<Double>
    suspend fun getAccountData(accountLocalId:Int, forceUpdate: Boolean):Resource<AccountModel>
    suspend fun setAsDefaultAccount(accountLocalId: Int):Resource<Boolean>
    suspend fun deleteAccount(accountLocalId: Int):Resource<Boolean>
    suspend fun uploadPendingChanges()
    suspend fun createAccount(title:String, total:Double, defaultAccount:Boolean):Resource<AccountModel>
    suspend fun updateAccount(accountLocalId: Int, title: String?, total:Double?, defaultAccount: Boolean?):Resource<AccountModel>
}