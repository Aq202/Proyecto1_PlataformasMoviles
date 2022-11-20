package com.example.proyecto_final_apps.data.repository

import android.content.Context
import com.example.proyecto_final_apps.data.Resource
import com.example.proyecto_final_apps.data.local.Database
import com.example.proyecto_final_apps.data.local.MyDataStore
import com.example.proyecto_final_apps.data.local.entity.ContactFullDataModel
import com.example.proyecto_final_apps.data.local.entity.ContactModel
import com.example.proyecto_final_apps.data.local.entity.ContactWithUserModel
import com.example.proyecto_final_apps.data.remote.API
import com.example.proyecto_final_apps.data.remote.ErrorParser
import com.example.proyecto_final_apps.data.remote.dto.getContactDataResponse.toContactModel
import com.example.proyecto_final_apps.data.remote.dto.requests.NewContactRequest
import com.example.proyecto_final_apps.data.remote.dto.toContactModel
import com.example.proyecto_final_apps.data.remote.dto.toDebtAcceptedModel
import com.example.proyecto_final_apps.data.remote.dto.toUserModel
import com.example.proyecto_final_apps.helpers.Internet
import javax.inject.Inject

class ContactRepositoryImp @Inject constructor(
    val api: API,
    val context: Context,
    val database: Database,
    val errorParser: ErrorParser,
    val debtRepository: DebtRepository
) : ContactRepository {
    override suspend fun getContactsList(forceUpdate: Boolean): Resource<List<ContactWithUserModel>> {

        uploadPendingChanges()

        try {

            val numberOfContacts = database.contactDao().getNumberOfContacts()
            if (numberOfContacts > 0 && !(forceUpdate && Internet.checkForInternet(context))) {
                //from database
                val accountList = database.contactDao().getContactsList()
                return Resource.Success(accountList.map{ contact ->
                    //search user
                    val user = database.userDao().getUser(contact.userAsContact)
                    ContactWithUserModel(contact, user!!)
                })

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

                        Resource.Success(contactsList.map{ contact ->
                            val user = database.userDao().getUser(contact.userAsContact)
                            ContactWithUserModel(contact, user!!)
                        })
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

    private suspend fun uploadPendingChanges() {

        //contacto por eliminar
        val contactsToDelete = database.contactDao().getAllContactsPendingToDelete()
        if (contactsToDelete.isNotEmpty()) {
            contactsToDelete.forEach {
                deleteContactInApi(it) //Trata de eliminar contacto y deudas
            }
        }

        //Deudas por crear
        val contactsToUpdate = database.contactDao().getAllContactsRequiringUpdate()
        contactsToUpdate.forEach {
            if (it.remoteId == null) {
                //crear nuevo contacto
                uploadNewContactToApi(it)
            } else {
                //Falta actualizar
            }
        }
    }


    override suspend fun getContactData(
        userId: String,
        forceUpdate: Boolean
    ): Resource<ContactFullDataModel> {

        uploadPendingChanges()

        val ds = MyDataStore(context)
        val token = ds.getValueFromKey("token") ?: return Resource.Error("No token")

        val contact = database.contactDao().getContactByUserAsContact(userId)
            ?: return Resource.Error("El contacto no existe.")
        val userAsContact = database.userDao().getUser(contact.userAsContact)
            ?: return Resource.Error("El usuario de contacto no existe.")

        if (contact.remoteId != null && forceUpdate && Internet.checkForInternet(context)) {

            //Descargar data de api

            try {

                val requestResult = api.getContactData(token, contact.remoteId!!)

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

    override suspend fun newContact(userId: String): Resource<ContactModel> {

        var contact = database.contactDao().getContactByUserAsContact(userId)

        if (contact != null) {
            return Resource.Success(contact)
        }

        //store in database.
        contact = ContactModel(remoteId = null, userAsContact = userId)
        val contactId = database.contactDao().insertContact(contact)
        contact.localId = contactId.toInt() //actualizar id

        val uploadResult = uploadNewContactToApi(contact)

        return if (uploadResult is Resource.Success) uploadResult
        else {
            contact.requiresUpdate = true
            database.contactDao().updateContact(contact)
            Resource.Success(contact) //Success, pues Se creó el usuario localmente
        }

    }

    private suspend fun uploadNewContactToApi(contact: ContactModel): Resource<ContactModel> {
        if (!Internet.checkForInternet(context)) return Resource.Error("No hay conexión a internet.")

        try {
            val ds = MyDataStore(context)
            val token = ds.getValueFromKey("token") ?: return Resource.Error("No token")

            //call to api
            val requestResult =
                api.newContact(token, NewContactRequest(contact.userAsContact, contact.localId!!))

            if (requestResult.isSuccessful) {

                val contactRes = requestResult.body()?.toContactModel()

                return if (contactRes != null) {
                    contact.remoteId = contactRes.remoteId
                    database.contactDao().updateContact(contact)
                    Resource.Success(contact)
                } else Resource.Error("No se obtuvieron datos al crear contacto.")


            } else {

                val errorBody = requestResult.errorBody()
                val error = errorParser.parseErrorObject(errorBody)
                println("DIEGO: ${error?.err}")

                //si ya existe el contacto en la api
                return if (error?.status == 409) {
                    //actualizar datos de  contactos
                    getContactsList(true)
                    //Obtener contacto
                    val result = getContactData(userId = contact.userAsContact, true)
                    if (result is Resource.Success) Resource.Success(result.data.contact)
                    else Resource.Error(
                        result.message ?: "No se ha podido obtener los datos del contacto."
                    )

                } else {
                    Resource.Error(error?.err ?: "Ocurrió un error en el servidor.")
                }
            }
        } catch (ex: Exception) {
            println("DIEGO: ${ex.message}")
            return Resource.Error("Ocurrió un error.")
        }
    }


    override suspend fun deleteContact(userId: String): Resource<Boolean> {


        val contact = database.contactDao().getContactByUserAsContact(userId)
            ?: return Resource.Error("No se encontró el contacto.")

        //La eliminación del contacto ejecuta implicitamente la eliminación de sus deudas en la API
        //Da lo mismo eliminarlas solo localmente
        val debts = database.debtDao().getAcceptedDebtsByUser(userId)
        debts.forEach { debt ->
            debtRepository.finalizeDebt(debt.localId!!, false) //No actualizar en api
        }

        //si solo esta guardada localmente, eliminar de la bd
        if (contact.remoteId == null) {
            database.contactDao().deleteContact(contact)
            return Resource.Success(true)
        }

        return deleteContactInApi(contact)
    }

    private suspend fun deleteContactInApi(contact: ContactModel): Resource<Boolean> {

        val ds = MyDataStore(context)
        val token = ds.getValueFromKey("token") ?: return Resource.Error("No token")

        if (Internet.checkForInternet(context)) {

            try {

                //borrar de la api
                val requestResult = api.deleteContact(token, contact.remoteId!!)

                if (requestResult.isSuccessful) {
                    //eliminar de la base de datos
                    database.contactDao().deleteContact(contact)

                    return Resource.Success(true)
                } else {
                    val errorBody = requestResult.errorBody()
                    val error = errorParser.parseErrorObject(errorBody)
                    println("DIEGO: ${error?.err}")

                }

            } catch (ex: Exception) {
                println("DIEGO: ${ex.message}")

            }

        }

        //si no se completo la eliminacion, marcar localmente
        if (!contact.deletionPending) {
            contact.deletionPending = true
            database.contactDao().updateContact(contact)
        }

        database.debtDao().setDeletionPendingToAcceptedDebtsByUserId(contact.userAsContact)

        return Resource.Success(false)
    }


}