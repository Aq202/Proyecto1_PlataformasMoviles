package com.example.proyecto_final_apps.data.repository

import android.content.Context
import com.example.proyecto_final_apps.data.Resource
import com.example.proyecto_final_apps.data.local.Database
import com.example.proyecto_final_apps.data.local.MyDataStore
import com.example.proyecto_final_apps.data.local.entity.OperationModel
import com.example.proyecto_final_apps.data.remote.API
import com.example.proyecto_final_apps.data.remote.dto.getOperationsResponse.toOperationModel
import com.example.proyecto_final_apps.helpers.DateParse
import com.example.proyecto_final_apps.helpers.Internet
import java.util.*
import javax.inject.Inject

class OperationRepositoryImp @Inject constructor(
    private val api: API,
    val context: Context,
    private val database: Database
) : OperationRepository {

    override suspend fun getOperations(forceUpdate: Boolean): Resource<List<OperationModel>> {

        try {

            val operationsStoredNumber = database.operationDao().getOperationsStoredNumber()
            if (operationsStoredNumber > 0 && !(forceUpdate && Internet.checkForInternet(context))) {

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
                    if (operationsList != null && operationsList.isNotEmpty()) {

                        //store in db
                        database.operationDao().deleteAllOperations()
                        database.operationDao().insertMany(operationsList)

                        return Resource.Success(operationsList)
                    } else
                        return Resource.Error("No hay operaciones por mostrar.")

                } else
                    println(result.message())

            }

        } catch (ex: Exception) {

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

    private fun filterOperationByDate(
        operations: List<OperationModel>,
        startDate: Date?,
        endDate: Date?
    ): List<OperationModel> {

        return operations.filter {
            val operationDate = DateParse.formatDate(it.date)
            if (startDate != null && endDate != null) {
                operationDate >= startDate && operationDate <= endDate
            } else if (startDate != null && endDate == null)
                operationDate >= startDate
            else if (endDate != null)
                operationDate <= endDate
            else true
        }

    }

    override suspend fun getAccountOperations(
        localAccountId: Int,
        forceUpdate: Boolean,
        startDate: Date?,
        endDate: Date?
    ): Resource<List<OperationModel>> {

        var storedOperations = database.operationDao().getAccountOperations(localAccountId)

        if (storedOperations.isNotEmpty() && !(forceUpdate && Internet.checkForInternet(context))) {

            //filtrar y retornar operaciones de la bd
            val filteredOperations = filterOperationByDate(storedOperations, startDate, endDate)
            return if (filteredOperations.isNotEmpty())
                Resource.Success(filteredOperations)
            else Resource.Error("No se encontraron operaciones dentro del rango de fechas.")
        } else {
            //download operations from api
            when (val result = getOperations(true)) {
                is Resource.Success -> {

                    //filtrar y retornar operaciones de la bd
                    storedOperations = database.operationDao().getAccountOperations(localAccountId)
                    return if (storedOperations.isNotEmpty()) {
                        val filteredOperations =
                            filterOperationByDate(storedOperations, startDate, endDate)
                        if (filteredOperations.isNotEmpty())
                            Resource.Success(filteredOperations)
                        else Resource.Error("No se encontraron operaciones dentro del rango de fechas.")

                        //No se encontró ninguna operacion de la cuenta
                    } else Resource.Error("No operations founded.")

                }
                else -> return result //Estado de error de getOperations
            }
        }

    }
}