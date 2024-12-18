package com.example.proyecto_final_apps.data.repository

import android.content.Context
import com.example.proyecto_final_apps.R
import com.example.proyecto_final_apps.data.Category
import com.example.proyecto_final_apps.data.Resource
import com.example.proyecto_final_apps.data.local.Database
import com.example.proyecto_final_apps.data.local.MyDataStore
import com.example.proyecto_final_apps.data.local.entity.*
import com.example.proyecto_final_apps.data.remote.API
import com.example.proyecto_final_apps.data.remote.ErrorParser
import com.example.proyecto_final_apps.data.remote.dto.getContactDataResponse.toContactModel
import com.example.proyecto_final_apps.data.remote.dto.requests.NewDebtRequest
import com.example.proyecto_final_apps.data.remote.dto.toAccountModel
import com.example.proyecto_final_apps.data.remote.dto.toDebtAcceptedModel
import com.example.proyecto_final_apps.data.remote.dto.toUserModel
import com.example.proyecto_final_apps.helpers.DateParse
import com.example.proyecto_final_apps.helpers.Internet

class DebtRepositoryImp(
    val api: API,
    val context: Context,
    val database: Database,
    val errorParser: ErrorParser,
    val operationRepository: OperationRepository
) : DebtRepository {


    override suspend fun createDebt(
        contactUserId: String,
        active: Boolean,
        amount: Double,
        accountLocalId: Int,
        description: String?,
    ): Resource<DebtAcceptedModel> {

        val contact =
            database.contactDao().getContactByUserAsContact(contactUserId) ?: return Resource.Error(
                "No se encontró al contacto."
            )

        val account = database.accountDao().getAccountById(accountLocalId) ?: return Resource.Error(
            "No se encontró la cuenta."
        )

        //crear la correspondiente operacion
        val opResult =
            createDebtOperation(contactUserId, active, amount, account, description)

        if (opResult is Resource.Error) return Resource.Error(
            "Error al crear la operación de deuda: " + (opResult.message
                ?: "No se pudo crear la operación.")
        )


        //guardar en la bd
        val acceptedDebt = DebtAcceptedModel(
            remoteId = null,
            accountInvolved = accountLocalId,
            active = !active,  //Cuando se obtiene ingresos de una deuda, esta es negativa y viceversa
            amount = amount,
            userInvolved = contactUserId,
            description = description
        )
        val localId = database.debtDao().insertAcceptedDebt(acceptedDebt)
        acceptedDebt.localId = localId.toInt()

        //Agregar operación a cuenta de deudas

        //Subir a la api
        val result = uploadNewAcceptedDebtToApi(acceptedDebt, contact, account)

        return if (result is Resource.Success) result
        else {
            //No  se actualizó en la api, marcar como pendiente
            acceptedDebt.requiresUpdate = true
            database.debtDao().updateAcceptedDebt(acceptedDebt)

            Resource.Success(acceptedDebt)
        }

    }


    private suspend fun uploadNewAcceptedDebtToApi(
        debt: DebtAcceptedModel,
        contact: ContactModel? = null,
        account: AccountModel? = null
    ): Resource<DebtAcceptedModel> {

        if (!Internet.checkForInternet(context)) return Resource.Error("No hay conexión a internet.")

        try {

            //buscar cuenta
            val accountModel = account ?: database.accountDao().getAccountById(debt.accountInvolved)
            ?: return Resource.Error(
                "No se encontró la cuenta."
            )
            if (accountModel.remoteId == null) return Resource.Error("La cuenta no ha sido subida a la Api.")

            //Si no se recibió el contacto, buscarlo
            val targetContact =
                contact ?: database.contactDao().getContactByUserAsContact(debt.userInvolved)
                ?: return Resource.Error(
                    "No se encontró al contacto."
                )

            val ds = MyDataStore(context)
            val token = ds.getValueFromKey("token") ?: return Resource.Error("No token")

            val requestResult =
                api.newDebt(
                    token,
                    NewDebtRequest(
                        localId = debt.localId!!,
                        contactId = targetContact.remoteId!!,
                        active = debt.active,
                        amount = debt.amount,
                        account = accountModel.remoteId,
                        description = debt.description
                    )
                )

            if (requestResult.isSuccessful) {

                val remoteId = requestResult.body()?.id

                if (remoteId != null) {

                    //actualizar el remoteId de la deuda
                    debt.remoteId = remoteId
                    database.debtDao().updateAcceptedDebt(debt)

                    return Resource.Success(debt)

                } else return Resource.Error("No se obtuvo una respuesta de la Api.")

            } else {
                val errorBody = requestResult.errorBody()
                val error = errorParser.parseErrorObject(errorBody)
                println("DIEGO: ${error?.err}")

                return Resource.Error(error?.err ?: "No se pudo crear la deuda en la Api.")
            }

        } catch (ex: Exception) {
            println("DIEGO: ${ex.message}")
            return Resource.Error("Ocurrió un error.")
        }
    }

    private suspend fun uploadChanges() {

        //Operaciones pendientes de finalizar (eliminar)
        val debtsToFinalize = database.debtDao().getAllAcceptedDebtsPendingToDelete()
        debtsToFinalize.forEach { finalizeDebtInApi(it) }

        //Operaciones pendientes de crearse
        val debtsToCreate = database.debtDao().getAllAcceptedDebtsRequiringUpdate()
        debtsToCreate.forEach {
            if (it.remoteId == null) uploadNewAcceptedDebtToApi(it)
        }

    }

    private suspend fun createDebtOperation(
        contactUserId: String,
        active: Boolean,
        amount: Double,
        account: AccountModel,
        description: String?,
    ): Resource<OperationModel> {

        //obtener usuario
        val user = database.userDao().getUser(contactUserId)
            ?: return Resource.Error("No se encontró al usuario.")


        val title = context.getString(
            if (!active) R.string.active_debt_title
            else R.string.passive_debt_title,
            context.getString(R.string.alias_format, user.alias)
        )

        val category = Category(context).getDebtsCategory()!!

        //crear operacion en cuenta de deudas
        val debtAccount = database.accountDao().getDebtsAccount()
            ?: return Resource.Error("No se encontró la cuenta de deudas.")

        val debtOperationResult = operationRepository.createOperation(
            title = title,
            accountLocalId = debtAccount.localId!!,
            amount = amount,
            active = !active, //Operación contraria a la de la cuenta objetivo
            description = description,
            category = category.id,
            favorite = false,
            date = DateParse.getCurrentDate(),
            imgUrl = user.getRelativeImgUrl()
        )

        if (debtOperationResult is Resource.Error) return debtOperationResult

        //crear operación en cuenta objetivo
        return operationRepository.createOperation(
            title = title,
            accountLocalId = account.localId!!,
            amount = amount,
            active = active,
            description = description,
            category = category.id,
            favorite = false,
            date = DateParse.getCurrentDate(),
            imgUrl = user.getRelativeImgUrl()
        )


    }

    override suspend fun finalizeDebt(
        debtLocalID: Int,
        updateRemotely: Boolean
    ): Resource<Boolean> {

        val debt = database.debtDao().getAcceptedDebt(debtLocalID)
            ?: return Resource.Error("La deuda no existe.")

        val userInvolved = database.userDao().getUser(debt.userInvolved)
            ?: return Resource.Error("No se encontró al usuario objetivo.")

        val debtAccount = database.accountDao().getDebtsAccount()
            ?: return Resource.Error("La cuenta de deudas no existe.")

        if (debt.remoteId == null || !updateRemotely) {
            //No existe remotamente o no se desea actualizar, eliminar de la db
            database.debtDao().deleteAcceptedDebt(debt)
        } else {
            val deleteResult = finalizeDebtInApi(debt)

            if (deleteResult is Resource.Error && !debt.deletionPending) {
                //No se logró actualizar en la api
                //Marcar como pendiente de eliminación
                debt.deletionPending = true
                database.debtDao().updateAcceptedDebt(debt)

            }

        }

        val title = context.getString(
            if (debt.active)
                R.string.passive_debt_finalization_title
            else
                R.string.active_debt_finalization_title,
            context.getString(R.string.alias_format, userInvolved.alias)
        )

        val debtCategory = Category(context).getDebtsCategory()!!

        //Injectar operación contraria en cuenta de deudas
        operationRepository.createOperation(
            title = title,
            accountLocalId = debtAccount.localId!!,
            amount = debt.amount,
            active = !debt.active, //Operación contraria a la de la cuenta objetivo
            description = context.getString(R.string.debt_finalization_description),
            category = debtCategory.id,
            favorite = false,
            date = DateParse.getCurrentDate(),
            imgUrl = userInvolved.getRelativeImgUrl()
        )

        return Resource.Success(true)

    }

    override suspend fun getAcceptedDebtData(acceptedDebtLocalId: Int): Resource<DebtWithContactModel> {

        //Las deudas se descargan con el getDebtList
        //Solo obtener de la BD

        uploadChanges() //Subir datos pendientes a API

        val debt = database.debtDao().getAcceptedDebt(acceptedDebtLocalId) ?: return Resource.Error(
            "La deuda no existe"
        )
        val contact = database.userDao().getUser(debt.userInvolved)
            ?: return Resource.Error("No se encontró al contacto.")

        return Resource.Success(DebtWithContactModel(contact, debt))


    }

    override suspend fun getDebtList(forceUpdate: Boolean): Resource<List<Pair<DebtAcceptedModel, UserModel>>> {

        val ds = MyDataStore(context)
        val token = ds.getValueFromKey("token") ?: return Resource.Error("No token")


        uploadChanges() //Subir datos pendientes a API

        if (forceUpdate && Internet.checkForInternet(context)) {

            //Descargar data de api

            try {

                val requestResult = api.getDebtsList(token)

                if (requestResult.isSuccessful) {

                    //Eliminar datos de la bd local
                    database.debtDao().deleteAllAcceptedDebts()

                    val debtList = requestResult.body()

                    if (debtList?.isNotEmpty() == true) {

                        val debtsModelList = debtList.map { it.toDebtAcceptedModel() }

                        //Convertir a parejas Deuda, usuario
                        val debtAndUser = debtList.map {
                            Pair(
                                it.toDebtAcceptedModel(),
                                it.userInvolved.toUserModel()
                            )
                        }

                        //guardar datos
                        database.debtDao().insertManyAcceptedDebts(debtsModelList)

                        return Resource.Success(debtAndUser)
                    }

                } else {

                    val errorBody = requestResult.errorBody()
                    val error = errorParser.parseErrorObject(errorBody)
                    println("DIEGO: ${error?.err}")

                }
            } catch (ex: Exception) {
                println("DIEGO: ${ex.message}")
            }

        }

        //Cant get data from api
        //Return data from db

        val debts = database.debtDao().getAcceptedDebts()
        return if (debts.isNotEmpty()) {
            //Obtener usuario por cada deuda
            val debtWithUser = debts.map {

                val account = database.userDao().getUser(it.userInvolved)
                    ?: return Resource.Error("No existe el usuario para una de las deudas.")
                Pair(it, account)
            }

            Resource.Success(debtWithUser)

        } else Resource.Error("No se encontraron deudas.")

    }


    private suspend fun finalizeDebtInApi(debt: DebtAcceptedModel): Resource<Boolean> {

        val ds = MyDataStore(context)
        val token = ds.getValueFromKey("token") ?: return Resource.Error("No token")

        if (!Internet.checkForInternet(context)) return Resource.Error("No hay internet.")
        if (debt.remoteId == null) return Resource.Error("La deuda no posee un id remoto.")

        try {
            //Actualizar en la api
            val deleteResult = api.deleteDebt(token, debt.remoteId!!)

            return if (deleteResult.isSuccessful || deleteResult.code() == 404) {
                //Eliminar de la base de datos
                database.debtDao().deleteAcceptedDebt(debt)
                Resource.Success(true)
            } else {
                val errorBody = deleteResult.errorBody()
                val error = errorParser.parseErrorObject(errorBody)
                println("DIEGO: ${error?.err}")

                Resource.Error(error?.err ?: "No se eliminó en la api.")
            }
        } catch (ex: Exception) {
            println("DIEGO: ${ex.message}")
            return Resource.Error("Ocurrió un error.")
        }
    }

    override suspend fun completeDebt(
        debtLocalId: Int,
        targetAccountId: Int
    ): Resource<OperationModel> {

        val debt = database.debtDao().getAcceptedDebt(debtLocalId)
            ?: return Resource.Error("No se encontró la deuda.")
        val user = database.userDao().getUser(debt.userInvolved)
            ?: return Resource.Error("No se encontró el usuario del contacto.")
        val debtAccount = database.accountDao().getDebtsAccount()
            ?: return Resource.Error("No se encontró la cuenta de deudas.")

        val title =
            context.getString(
                (if (debt.active) R.string.active_debt_complete_title else R.string.passive_debt_complete_title),
                context.getString(R.string.alias_format, user.alias)
            )

        //Inyectar operacion en cuenta objetivo
        val operationResult = operationRepository.createOperation(
            title = title,
            accountLocalId = targetAccountId,
            amount = debt.amount,
            active = debt.active, //Si es deuda con alguien (false) se retira el dinero de la cuenta objetivo
            description = debt.description,
            category = Category(context).getDebtsCategory()!!.id,
            favorite = false,
            date = DateParse.getCurrentDate(),
            imgUrl = user.getRelativeImgUrl()
        )

        val operation: OperationModel
        if (operationResult is Resource.Success) operation = operationResult.data
        else return Resource.Error(
            operationResult.message ?: "No se pudo crear la operación en la cuenta."
        )


        //Realizar operación en cuenta de deudas para balancear el saldo
        operationRepository.createOperation(
            title = title,
            accountLocalId = debtAccount.localId!!,
            amount = debt.amount,
            active = !debt.active, //Si es deuda con alguien (false) se deposita el dinero en la cuenta deudas (balancear lo que se retiró antes)
            description = debt.description,
            category = Category(context).getDebtsCategory()!!.id,
            favorite = false,
            date = DateParse.getCurrentDate(),
            imgUrl = user.getRelativeImgUrl()
        )

        //Finalizar deuda
        val finalizeResult = finalizeDebtInApi(debt)

        if (finalizeResult is Resource.Error) {
            //Marcar para posterior finalizacion
            debt.deletionPending = true
            database.debtDao().updateAcceptedDebt(debt)
        }

        return Resource.Success(operation)
    }


}