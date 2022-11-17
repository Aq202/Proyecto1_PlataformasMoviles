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
import com.example.proyecto_final_apps.data.remote.dto.requests.NewDebtRequest
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
            active = active,
            amount = amount,
            userInvolved = contactUserId,
            description = description
        )
        val localId = database.debtDao().insertAcceptedDebt(acceptedDebt)
        acceptedDebt.localId = localId.toInt()

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


        val title = if (active) context.getString(
            R.string.active_debt_title,
            user.name
        ) else context.getString(R.string.passive_debt_title, user.name)

        val category = Category(context).getDebtsCategory()!!

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
}