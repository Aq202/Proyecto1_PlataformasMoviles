package com.example.proyecto_final_apps.data.repository

import android.content.Context
import com.example.proyecto_final_apps.data.Resource
import com.example.proyecto_final_apps.data.local.Database
import com.example.proyecto_final_apps.data.local.MyDataStore
import com.example.proyecto_final_apps.data.local.entity.OperationModel
import com.example.proyecto_final_apps.data.remote.API
import com.example.proyecto_final_apps.data.remote.dto.operationResponse.toOperationModel
import javax.inject.Inject

class OperationRepositoryImp @Inject constructor(
    private val api: API,
    val context: Context,
    private val database: Database
) : OperationRepository {

    override suspend fun getOperations(forceUpdate: Boolean): Resource<List<OperationModel>> {


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
}