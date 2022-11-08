package com.example.proyecto_final_apps.data.repository

import android.content.Context
import com.example.proyecto_final_apps.data.Resource
import com.example.proyecto_final_apps.data.local.Database
import com.example.proyecto_final_apps.data.local.MyDataStore
import com.example.proyecto_final_apps.data.local.entity.AccountModel
import com.example.proyecto_final_apps.data.remote.API
import com.example.proyecto_final_apps.data.remote.dto.accountListResponse.toAccountModel
import com.example.proyecto_final_apps.helpers.DateParse
import com.example.proyecto_final_apps.helpers.Internet
import javax.inject.Inject

class AccountRepositoryImp @Inject constructor(
    val api: API,
    val context: Context,
    val database: Database
):AccountRepository {

    override suspend fun getAccountList(forceUpdate:Boolean): Resource<List<AccountModel>> {
        try{

        val numberOfAccounts = database.accountDao().getNumberOfAccounts()
        if(numberOfAccounts > 0 && !(forceUpdate && Internet.checkForInternet(context))){
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

                return if(accountList != null && accountList.isNotEmpty()){

                    //store in database
                    database.accountDao().deleteAll()
                    database.accountDao().insertManyAccounts(accountList)

                    Resource.Success(accountList)
                }else
                    Resource.Error("No se obtuvo ningúna cuenta.")
            }
        }
        }catch(ex:Exception){
            println("Diego: ${ex.message} ")
        }
        return Resource.Error("Error al obtener lista de cuentas.")
    }

    override suspend fun getAccountName(accountLocalId:Int, forceUpdate: Boolean):Resource<String>{
        val account = database.accountDao().getAccountById(accountLocalId)
        if(!(forceUpdate && Internet.checkForInternet(context)))
        return Resource.Success(account!!.title)
        else{
            val accountsResult = getAccountList(true)
            return if(accountsResult is Resource.Success) {
                val accountUpdated = database.accountDao().getAccountById(accountLocalId)
                if(accountUpdated != null)
                    Resource.Success(accountUpdated.title)
                else Resource.Error("La cuenta ha sido eliminada remotamente.")

            }else{
                //no se obtuvieron resultados remotos
                if(account != null) Resource.Success(account.title)
                else Resource.Error("No se encontró la cuenta.")
            }

        }

    }

    override suspend fun getAccountBalance(accountLocalId:Int): Resource<Double> {
        var ballance = 0.0
        val operationsList = database.operationDao().getAccountOperations(accountLocalId)

        if (operationsList.isEmpty())
            return Resource.Success(0.00)

        operationsList.forEach { operation ->
            if (operation.active) ballance += operation.amount
            else ballance -= operation.amount
        }

        return Resource.Success(ballance)
    }


    override suspend fun getAccountBalanceMovement(accountLocalId:Int): Resource<Double> {

        val balanceResult = getAccountBalance(accountLocalId)
        val operationsList = database.operationDao().getAccountOperations(accountLocalId)

        if (balanceResult is Resource.Success && operationsList.isNotEmpty()) {
            val currentBalance = balanceResult.data
            var lastBalance = 0.0


            operationsList.forEach { op ->

                val operationDate = DateParse.formatDate(op.date)
                val firstDayMonthDate = DateParse.getFirstDayOfMonthDate()

                if (firstDayMonthDate > operationDate) {
                    if (op.active) lastBalance += op.amount
                    else lastBalance -= op.amount
                }

            }

            return Resource.Success(currentBalance - lastBalance)
        }

        return Resource.Success(0.00)
    }

}