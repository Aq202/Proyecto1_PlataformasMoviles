package com.example.proyecto_final_apps.data.repository

import android.content.Context
import com.example.proyecto_final_apps.data.Resource
import com.example.proyecto_final_apps.data.local.Database
import com.example.proyecto_final_apps.data.local.MyDataStore
import com.example.proyecto_final_apps.data.local.entity.ContactFullDataModel
import com.example.proyecto_final_apps.data.local.entity.ContactModel
import com.example.proyecto_final_apps.data.remote.API
import com.example.proyecto_final_apps.data.remote.ErrorParser
import com.example.proyecto_final_apps.data.remote.dto.contactListResponse.toContactModel
import com.example.proyecto_final_apps.data.remote.dto.getContactDataResponse.toContactModel
import com.example.proyecto_final_apps.data.remote.dto.toDebtAcceptedModel
import com.example.proyecto_final_apps.data.remote.dto.toUserModel
import com.example.proyecto_final_apps.helpers.Internet
import javax.inject.Inject

class ContactRepositoryImp @Inject constructor(
    val api: API,
    val context: Context,
    val database: Database,
    val errorParser: ErrorParser
) : ContactRepository {
    override suspend fun getContactsList(forceUpdate: Boolean): Resource<List<ContactModel>> {

        try {

            val numberOfContacts = database.contactDao().getNumberOfContacts()
            if (numberOfContacts > 0 && !(forceUpdate && Internet.checkForInternet(context))) {
                //from database
                val accountList = database.contactDao().getContactsList()
                return Resource.Success(accountList)

            } else {

                val ds = MyDataStore(context)
                val token = ds.getValueFromKey("token") ?: return Resource.Error("No token")

                //from api
                val result = api.getContactsList(token)

                if (result.isSuccessful) {
                    val contactsDto = result.body()
                    val contactsList = contactsDto?.map { it.toContactModel() }

                    //delete all contacts in db
                    database.contactDao().deleteAll()

                    return if (contactsList != null && contactsList.isNotEmpty()) {

                        //store database
                        database.contactDao().insertManyContacts(contactsList)

                        //store user data
                        contactsDto.forEach { contact ->
                            database.userDao().insertUser(contact.userAsContact.toUserModel())
                        }

                        Resource.Success(contactsList)
                    } else
                        Resource.Error("No se obtuvo ningúna cuenta.")
                }
            }
        } catch (ex: Exception) {
            println("Diego: ${ex.message} ")
            return Resource.Error(ex.message ?: "Ocurrió un error.")
        }
        return Resource.Error("Error al obtener lista de cuentas.")
    }


    override suspend fun getContactData(
        userId: String,
        forceUpdate: Boolean
    ): Resource<ContactFullDataModel> {

        val ds = MyDataStore(context)
        val token = ds.getValueFromKey("token") ?: return Resource.Error("No token")

        val contact = database.contactDao().getContactByUserAsContact(userId)
            ?: return Resource.Error("El contacto no existe.")
        val userAsContact = database.userDao().getUser(contact.userAsContact)
            ?: return Resource.Error("El usuario de contacto no existe.")

        if (forceUpdate && Internet.checkForInternet(context)) {

            //Descargar data de api

            try {

                val requestResult = api.getContactData(token, contact.remoteId)

                if (requestResult.isSuccessful) {

                    val contactData = requestResult.body()

                    if (contactData != null) {

                        val contactModel = contactData.toContactModel()
                        var debtsAccepted = contactData.debtsAccepted?.map {
                            it.toDebtAcceptedModel()
                        }
                        if (debtsAccepted?.isEmpty() == true) debtsAccepted = null

                        //guardar contacto en db
                        database.contactDao().insertContact(contactModel)

                        //guardar deudas aceptadas
                        debtsAccepted?.let {
                            database.debtDao().insertManyAcceptedDebts(it)
                        }

                        return Resource.Success(
                            ContactFullDataModel(
                                contact = contactModel,
                                userAsContactData = userAsContact,
                                debtsAccepted
                            )
                        )

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

        //Return data from db
        val debtsAccepted = database.debtDao().getAcceptedDebtsByUser(contact.userAsContact)
        return Resource.Success(
            ContactFullDataModel(
                contact = contact,
                userAsContactData = userAsContact,
                debtsAccepted
            )
        )


    }


}