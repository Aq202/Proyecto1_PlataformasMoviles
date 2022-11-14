package com.example.proyecto_final_apps.data.repository

import android.content.Context
import com.example.proyecto_final_apps.data.Resource
import com.example.proyecto_final_apps.data.local.Database
import com.example.proyecto_final_apps.data.local.MyDataStore
import com.example.proyecto_final_apps.data.local.entity.OperationModel
import com.example.proyecto_final_apps.data.remote.API
import com.example.proyecto_final_apps.data.remote.dto.operationDto.toOperationModel
import com.example.proyecto_final_apps.data.remote.dto.requests.NewOperationRequest
import com.example.proyecto_final_apps.helpers.DateParse
import com.example.proyecto_final_apps.helpers.Internet
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.*
import javax.inject.Inject

class OperationRepositoryImp @Inject constructor(
    private val api: API,
    val context: Context,
    private val database: Database
) : OperationRepository {

    private val accountRepository: AccountRepository = AccountRepositoryImp(api,context,database)

    override suspend fun getOperations(forceUpdate: Boolean): Resource<List<OperationModel>> {

        try {
            uploadPendingChanges()

            val operationsStoredNumber = database.operationDao().getOperationsStoredNumber()
            if (operationsStoredNumber > 0 && !(forceUpdate && Internet.checkForInternet(context))) {

                //local data
                val operationsList = sortOperationsByDate(database.operationDao().getAllOperations())
                return Resource.Success(operationsList)
            } else {

                val ds = MyDataStore(context)
                val token = ds.getValueFromKey("token") ?: return Resource.Error("No token")

                //From api
                val result = api.getAllOperations(token)

                if (result.isSuccessful) {
                    val operationsList = result.body()?.operations?.map { it.toOperationModel() }
                    return if (operationsList != null && operationsList.isNotEmpty()) {

                        //store in db
                        database.operationDao().deleteAllOperations()
                        database.operationDao().insertMany(operationsList)

                        Resource.Success(sortOperationsByDate(operationsList))
                    } else
                        Resource.Error("No hay operaciones por mostrar.")

                } else
                    println("Erick: "+ result.message())

            }

        } catch (ex: Exception) {

        }

        return Resource.Error("Error al obtener operaciones.")
    }

    private fun sortOperationsByDate(operations:List<OperationModel>):List<OperationModel>{

        val mutableOperationsList = operations as MutableList
        mutableOperationsList.sortByDescending { DateParse.formatDate(it.date) }
        return mutableOperationsList
    }

    override suspend fun getGeneralBalance(): Resource<Double> {

        var ballance = 0.0;
        val operationsList = database.operationDao().getAllOperations()

        if (operationsList.isEmpty())
            return Resource.Success(0.00)

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

    override suspend fun getOperationData(
        operationLocalId: Int,
        forceUpdate: Boolean
    ): Resource<OperationModel> {

        uploadPendingChanges()

        val operation = database.operationDao().getOperationById(operationLocalId)
        if (!(forceUpdate && Internet.checkForInternet(context)))
            return Resource.Success(operation!!)
        else {
            val operationsResult = getOperations(true)
            return if (operationsResult is Resource.Success) {
                val operationUpdated = database.operationDao().getOperationById(operationLocalId)
                if (operationUpdated != null)
                    Resource.Success(operationUpdated)
                else Resource.Error("La operación ha sido eliminada remotamente.")

            } else {
                //no se obtuvieron resultados remotos
                if (operation != null) Resource.Success(operation)
                else Resource.Error("No se encontró la operation.")
            }

        }
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

    override suspend fun deleteOperation(operationLocalId: Int): Resource<Boolean> {

        val ds = MyDataStore(context)
        val token = ds.getValueFromKey("token") ?: return Resource.Error("No token")

        val operation = database.operationDao().getOperationById(operationLocalId)
            ?: return Resource.Error("La operación no existe.")

        //delete in api
        if (Internet.checkForInternet(context) && operation.remoteId != null) {

            try {
                val result = api.deleteOperation(token, operation.remoteId)
                if (result.isSuccessful) {
                    //Eliminar completamente de la bd
                    val deletedCount = database.operationDao().deleteOperation(operation)
                    if (deletedCount > 0) {
                        //Actualizar monto de la cuenta

                        val accountRequest = accountRepository.getAccountData(operation.accountLocalId,false)
                        val newTotal = account.
                        //val newTotal = if(operation.active) account!!.total + operation.amount else account!!.total - operation.amount
                        accountRepository.updateAccount(operation.accountLocalId, title = account!!.title, total = newTotal, account.defaultAccount)

                        return Resource.Success(true)
                    }
                }
            } catch (ex: Exception) {
                println("Diego: ${ex.message}")
            }
        }

        //No se pudo eliminar en la api

        //Marcar para late delete
        account.deletionPending = true
        database.accountDao().updateAccount(account)
        database.operationDao().setDeletionPendingToAccountOperations(accountLocalId)

        //Si la operación es la favorita, sustituirla
        if (account.defaultAccount) {
            deselectDefaultAccountLocally() //deseleccionar como favorita
            val editableAccounts = database.accountDao().getEditableAccounts()
            if (editableAccounts.isNotEmpty()) {
                editableAccounts[0].localId?.let { setAsDefaultAccount(it) }
            }
        }

        return Resource.Success(true)
    }

    override suspend fun uploadPendingChanges() {
        //Eliminar operaciones pendientes
        val operationsToDelete = database.operationDao().getAllPendingToDeleteOperation()
        if (operationsToDelete.isNotEmpty()) {
            operationsToDelete.forEach { operation ->
                deleteOperation(operation.localId!!)
            }
        }

        //Operaciones pendientes
        val accountsToUpdate = database.accountDao().getAllAccountsRequiringUpdate()
        if(accountsToUpdate.isNotEmpty()){

            //Operaciones que no se han creado
            accountsToUpdate.filter { it.remoteId == null }.forEach{ account ->
                uploadNewAccountToApi(account)
            }

            //Operaciones por actualizar
            accountsToUpdate.filter { it.remoteId != null }.forEach{account ->
                uploadAccountUpdatesToApi(account)
            }
        }

    }

    override suspend fun createOperation(
        title: String,
        accountRemoteId: String,
        accountLocalId: Int,
        amount: Double,
        active: Boolean,
        description: String?,
        category: Int,
        favorite: Boolean,
        date: String,
        imgUrl:String?
    ): Resource<OperationModel> {
        val operationCreated =
            OperationModel(
                accountRemoteId = accountRemoteId,
                accountLocalId = accountLocalId,
                active = active,
                amount = amount,
                category = category,
                favorite = favorite,
                date = date,
                title = title,
                description = description,
                imgUrl = imgUrl
            )
        operationCreated.localId = database.accountDao().insertOperation(
            OperationModel(
                accountRemoteId = accountRemoteId,
                accountLocalId = accountLocalId,
                active = active,
                amount = amount,
                category = category,
                favorite = favorite,
                date = date,
                title = title,
                description = description,
                imgUrl = imgUrl
            )
        ).toInt()

        //Subir datos a la api
        val requestResult = uploadNewOperationToApi(operationCreated)
        if(requestResult is Resource.Success) return requestResult
        else println("Erick: ${requestResult.message}")



        //Si no se completó la solicitud a la api
        operationCreated.requiresUpdate = true
        database.operationDao().updateOperation(operationCreated)

        //Actualizar monto de la cuenta

        return Resource.Success(operationCreated)
    }

    private suspend fun uploadNewOperationToApi(operation:OperationModel):Resource<OperationModel>{
        if (Internet.checkForInternet(context)) {

            try {

                val ds = MyDataStore(context)
                val token = ds.getValueFromKey("token") ?: return Resource.Error("No token")

                val requestResult = api.createOperation(
                    token,
                    NewOperationRequest(
                        account = operation.accountRemoteId!!,
                        amount = operation.amount,
                        category = operation.category,
                        date = operation.date,
                        favourite = operation.favorite,
                        localId = operation.localId!!,
                        title = operation.title,
                        description = operation.description,
                        imgUrl = operation.imgUrl,
                        active = operation.active
                    )
                )

                if (requestResult.isSuccessful) {

                    val account = requestResult.body()?.account
                    val newTotal = if(operation.active) account!!.total + operation.amount else account!!.total - operation.amount
                    accountRepository.updateAccount(account!!.localId, title = account!!.title, total = newTotal, account.defaultAccount)

                    requestResult.body()?.toOperationModel()?.let { operationDataFromApi ->
                        //update db data
                        database.operationDao().updateOperation(operationDataFromApi)
                        return Resource.Success(operationDataFromApi)
                    }


                } else return Resource.Error(requestResult.errorBody().toString())
            } catch (ex: Exception) {
                return Resource.Error("Catch: "+ex.message ?:"")
            }
        }
        return Resource.Error("No internet.")
    }
}