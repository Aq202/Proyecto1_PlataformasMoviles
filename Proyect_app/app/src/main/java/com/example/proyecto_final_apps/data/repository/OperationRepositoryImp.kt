package com.example.proyecto_final_apps.data.repository

import android.content.Context
import com.example.proyecto_final_apps.data.Resource
import com.example.proyecto_final_apps.data.local.Database
import com.example.proyecto_final_apps.data.local.MyDataStore
import com.example.proyecto_final_apps.data.local.entity.OperationModel
import com.example.proyecto_final_apps.data.remote.API
import com.example.proyecto_final_apps.data.remote.dto.operationResponse.toOperationModel
import com.example.proyecto_final_apps.helpers.DateParse
import javax.inject.Inject

class OperationRepositoryImp @Inject constructor(
    private val api: API,
    val context: Context,
    private val database: Database
) : OperationRepository {

    override suspend fun getOperations(forceUpdate: Boolean): Resource<List<OperationModel>> {

        try{

        val operationsStoredNumber = database.operationDao().getOperationsStoredNumber()
        if (operationsStoredNumber > 0 && !forceUpdate) {

            //local data
            val operationsList = database.operationDao().getAllOperations()
            return Resource.Success(operationsList)
        } else {

            val ds = MyDataStore(context)
            val token = ds.getValueFromKey("token") ?: return Resource.Error("No token")

            //From api
            val result = api.getAllOperations(token)

            if (result.isSuccessful) {
                val operationsList = result.body()?.operations?.map { it.toOperationModel() }
                if (operationsList != null && operationsList.isNotEmpty()){

                    //store in db
                    database.operationDao().deleteAllOperations()
                    database.operationDao().insertMany(operationsList)

                    return Resource.Success(operationsList)
                }else
                    return Resource.Error("No hay operaciones por mostrar.")

            } else
                println(result.message())

        }

        }catch(ex:Exception){

        }

        return Resource.Error("Error al obtener operaciones.")
    }

    override suspend fun getGeneralBalance(): Resource<Double> {

        var ballance = 0.0;
        val operationsList = database.operationDao().getAllOperations()

        if (operationsList.isEmpty())
            return return Resource.Success(0.00)

        operationsList.forEach { operation ->
            if (operation.active) ballance += operation.amount
            else ballance -= operation.amount
        }

        return Resource.Success(ballance)
    }

    override suspend fun getBalanceMovement(): Resource<Double> {

        val balanceResult = getGeneralBalance()
        val operationsList = database.operationDao().getAllOperations()

        if( balanceResult is Resource.Success && operationsList.isNotEmpty()){
            val currentBalance = balanceResult.data
            var lastBalance = 0.0


            operationsList.forEach{ op ->

                val operationDate = DateParse.formatDate(op.date)
                val firstDayMonthDate = DateParse.getFirstDayOfMonthDate()

                if(firstDayMonthDate > operationDate){
                    if (op.active) lastBalance += op.amount
                    else lastBalance -= op.amount
                }

            }

            return Resource.Success(currentBalance - lastBalance)
        }

        return Resource.Success(0.00)
    }
}