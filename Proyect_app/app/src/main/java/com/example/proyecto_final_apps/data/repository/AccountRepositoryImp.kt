package com.example.proyecto_final_apps.data.repository

import android.content.Context
import com.example.proyecto_final_apps.data.Resource
import com.example.proyecto_final_apps.data.local.Database
import com.example.proyecto_final_apps.data.local.MyDataStore
import com.example.proyecto_final_apps.data.local.entity.AccountModel
import com.example.proyecto_final_apps.data.remote.API
import com.example.proyecto_final_apps.data.remote.dto.requests.NewAccountRequest
import com.example.proyecto_final_apps.data.remote.dto.toAccountModel
import com.example.proyecto_final_apps.helpers.DateParse
import com.example.proyecto_final_apps.helpers.Internet
import javax.inject.Inject

class AccountRepositoryImp @Inject constructor(
    val api: API,
    val context: Context,
    val database: Database
) : AccountRepository {

    override suspend fun getAccountList(forceUpdate: Boolean): Resource<List<AccountModel>> {

        try {

            uploadPendingChanges()

            val numberOfAccounts = database.accountDao().getNumberOfAccounts()
            if (numberOfAccounts > 0 && !(forceUpdate && Internet.checkForInternet(context))) {
                //from database
                val accountList = database.accountDao().getAccounts()
                return Resource.Success(accountList)

            } else {

                val ds = MyDataStore(context)
                val token = ds.getValueFromKey("token") ?: return Resource.Error("No token")


                //from api
                val result = api.getAccountList(token)

                if (result.isSuccessful) {
                    val accountList = result.body()?.accounts?.map { it.toAccountModel() }

                    return if (accountList != null && accountList.isNotEmpty()) {

                        //store in database
                        database.accountDao().deleteAll()
                        database.accountDao().insertManyAccounts(accountList)

                        Resource.Success(accountList)
                    } else
                        Resource.Error("No se obtuvo ningúna cuenta.")
                }
            }
        } catch (ex: Exception) {
            println("Diego: ${ex.message} ")
        }
        return Resource.Error("Error al obtener lista de cuentas.")
    }

    override suspend fun getAccountData(
        accountLocalId: Int,
        forceUpdate: Boolean
    ): Resource<AccountModel> {

        uploadPendingChanges()

        val account = database.accountDao().getAccountById(accountLocalId)
        if (!(forceUpdate && Internet.checkForInternet(context)))
            return Resource.Success(account!!)
        else {
            val accountsResult = getAccountList(true)
            return if (accountsResult is Resource.Success) {
                val accountUpdated = database.accountDao().getAccountById(accountLocalId)
                if (accountUpdated != null)
                    Resource.Success(accountUpdated)
                else Resource.Error("La cuenta ha sido eliminada remotamente.")

            } else {
                //no se obtuvieron resultados remotos
                if (account != null) Resource.Success(account)
                else Resource.Error("No se encontró la cuenta.")
            }

        }

    }

    override suspend fun setAsDefaultAccount(accountLocalId: Int): Resource<Boolean> {

        //actualizar en BD
        val previousDefaultAccount = database.accountDao().getDefaultAccount()
        val accountToUpdate = database.accountDao().getAccountById(accountLocalId)
            ?: return Resource.Error("No se ha encontrado la cuenta.")

        if (!accountToUpdate.editable) return Resource.Error("La cuenta no puede ser seleccionada como default.")

        previousDefaultAccount?.defaultAccount = false
        accountToUpdate.defaultAccount = true

        try {

            if (Internet.checkForInternet(context) && accountToUpdate.remoteId != null) {

                val ds = MyDataStore(context)
                val token = ds.getValueFromKey("token") ?: return Resource.Error("No token")

                //realizar peticion
                val result = api.setAsDefaultAccount(token, accountToUpdate.remoteId)
                if (result.isSuccessful) {
                    //actualizar en BD
                    if (previousDefaultAccount != null)
                        database.accountDao().updateAccount(previousDefaultAccount)
                    database.accountDao().updateAccount(accountToUpdate)

                    return Resource.Success(true)
                }
            }

        } catch (ex: Exception) {
            println("Diego: ${ex.message}")

        }
        //Si no se actualizó en el servidor
        previousDefaultAccount?.requiresUpdate = true
        accountToUpdate.requiresUpdate = true

        //actualizar en BD
        if (previousDefaultAccount != null)
            database.accountDao().updateAccount(previousDefaultAccount)
        database.accountDao().updateAccount(accountToUpdate)

        return Resource.Success(true)
    }

    override suspend fun deleteAccount(accountLocalId: Int): Resource<Boolean> {

        val ds = MyDataStore(context)
        val token = ds.getValueFromKey("token") ?: return Resource.Error("No token")

        val account = database.accountDao().getAccountById(accountLocalId)
            ?: return Resource.Error("La cuenta no existe.")

        if (!account.editable) return Resource.Error("La cuenta no puede ser eliminada.")


        //delete in api
        if (Internet.checkForInternet(context) && account.remoteId != null) {

            try {
                val result = api.deleteAccount(token, account.remoteId)
                if (result.isSuccessful) {
                    //Eliminar completamente de la bd
                    val deletedCount = database.accountDao().deleteAccount(account)
                    if (deletedCount > 0) {
                        //Eliminar operaciones relacionadas
                        database.operationDao().deleteAllAccountOperations(accountLocalId)

                        //Marcar nueva cuenta favorita en caso de haberse modificado
                        if (result.body()?.newDefaultAccount != null) {
                            val newDefaultAccountId = result.body()?.newDefaultAccount!!.localId
                            val newDefaultAccount =
                                database.accountDao().getAccountById(newDefaultAccountId)

                            newDefaultAccount?.defaultAccount = true
                            if (newDefaultAccount != null)
                                database.accountDao().updateAccount(newDefaultAccount)

                        }
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
        val editableAccounts = database.accountDao().getEditableAccounts()

        if (editableAccounts.isNotEmpty()) {
            editableAccounts[0].localId?.let { setAsDefaultAccount(it) }
        }

        return Resource.Success(true)
    }

    override suspend fun uploadPendingChanges() {
        val accountsToDelete = database.accountDao().getAllPendingToDeleteAccount()
        if(accountsToDelete.isNotEmpty()){
            accountsToDelete.forEach{ account ->
                deleteAccount(account.localId!!)
            }
        }
    }



    override suspend fun getAccountBalance(accountLocalId: Int): Resource<Double> {
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


    override suspend fun getAccountBalanceMovement(accountLocalId: Int): Resource<Double> {

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

    override suspend fun createAccount(
        title: String,
        total: Double,
        defaultAccount: Boolean
    ): Resource<AccountModel> {

        val accountCreated = AccountModel(title = title, total = total, defaultAccount = defaultAccount)
        accountCreated.localId = database.accountDao().insertAccount(AccountModel(title = title, total = total, defaultAccount = defaultAccount)).toInt()

        //Si se seleccionó como cuenta default, quitar la anterior
        database.accountDao().deselectDefaultAccount()

        if(Internet.checkForInternet(context)){

            try {

                val ds = MyDataStore(context)
                val token = ds.getValueFromKey("token") ?: return Resource.Error("No token")

                val requestResult = api.createAccount(
                    token,
                    NewAccountRequest(
                        localId = accountCreated.localId!!,
                        title = title,
                        total = total,
                        defaultAccount = defaultAccount
                    )
                )

                if (requestResult.isSuccessful) {

                    requestResult.body()?.toAccountModel()?.let { accountDataFromApi ->

                        //update db data
                        database.accountDao().updateAccount(accountDataFromApi)
                        return Resource.Success(accountDataFromApi)
                    }


                }else println(requestResult.errorBody().toString())
            }catch(ex:Exception){
                println("Diego: ${ex.message}")
            }
        }

        //Si no se completó la solicitud a la api
        accountCreated.requiresUpdate = true
        database.accountDao().updateAccount(accountCreated)
        return Resource.Success(accountCreated)
    }

}