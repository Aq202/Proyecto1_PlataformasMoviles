package com.example.proyecto_final_apps.data.repository

import android.content.Context
import com.example.proyecto_final_apps.data.Resource
import com.example.proyecto_final_apps.data.local.Database
import com.example.proyecto_final_apps.data.local.MyDataStore
import com.example.proyecto_final_apps.data.local.entity.ContactModel
import com.example.proyecto_final_apps.data.remote.API
import com.example.proyecto_final_apps.data.remote.dto.contactListResponse.toContactModel
import com.example.proyecto_final_apps.data.remote.dto.toUserModel
import com.example.proyecto_final_apps.helpers.Internet
import javax.inject.Inject

class ContactRepositoryImp @Inject constructor(
    val api: API,
    val context: Context,
    val database: Database
) : ContactRepository {
    override suspend fun getContactsList(forceUpdate:Boolean): Resource<List<ContactModel>> {

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
                    val contactsDto =   result.body()
                    val contactsList = contactsDto?.map { it.toContactModel() }

                    //delete all contacts in db
                    database.contactDao().deleteAll()

                    return if (contactsList != null && contactsList.isNotEmpty()) {

                        //store database
                        database.contactDao().insertManyContacts(contactsList)

                        //store user data
                       contactsDto.forEach{contact ->
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


}