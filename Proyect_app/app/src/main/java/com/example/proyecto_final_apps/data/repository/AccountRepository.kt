package com.example.proyecto_final_apps.data.repository

import com.example.proyecto_final_apps.data.Resource
import com.example.proyecto_final_apps.data.local.entity.AccountModel

interface AccountRepository {

    suspend fun getAccountList(forceUpdate:Boolean):Resource<List<AccountModel>>

}