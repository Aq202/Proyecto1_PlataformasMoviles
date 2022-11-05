package com.example.proyecto_final_apps.data.repository

import android.content.Context
import com.example.proyecto_final_apps.data.Resource
import com.example.proyecto_final_apps.data.local.Database
import com.example.proyecto_final_apps.data.local.MyDataStore
import com.example.proyecto_final_apps.data.local.entity.AccountModel
import com.example.proyecto_final_apps.data.remote.API
import com.example.proyecto_final_apps.data.remote.dto.accountListResponse.toAccountModel
import javax.inject.Inject

class AccountRepositoryImp @Inject constructor(
    val api: API,
    val context: Context,
    val database: Database
):AccountRepository {

    override suspend fun getAccountList(forceUpdate:Boolean): Resource<List<AccountModel>> {
        try{

        val numberOfAccounts = database.accountDao().getNumberOfAccounts()
        if(numberOfAccounts > 0 && !forceUpdate){
            //from database
            val accountList = database.accountDao().getAccounts()
            return Resource.Success(accountList)

        }else{

            val ds = MyDataStore(context)
            val token = ds.getValueFromKey("token") ?: return Resource.Error("No token")


            //from api
            val result = api.getAccountList(token)

            if(result.isSuccessful){
                val accountList = result.body()?.accounts?.map{it.toAccountModel()}

                if(accountList != null && accountList.isNotEmpty()){

                    //store in database
                    database.accountDao().deleteAll()
                    database.accountDao().insertManyAccounts(accountList)

                    return Resource.Success(accountList)
                }else
                    return Resource.Error("No se obtuvo ningúna cuenta.")
            }
        }
        }catch(ex:Exception){
            println("Diego: ${ex.message} ")
        }
        return Resource.Error("Error al obtener lista de cuentas.")
    }
}