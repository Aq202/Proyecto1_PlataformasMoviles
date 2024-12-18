package com.example.proyecto_final_apps.data.repository

import com.example.proyecto_final_apps.data.Resource
import com.example.proyecto_final_apps.data.local.entity.ContactFullDataModel
import com.example.proyecto_final_apps.data.local.entity.ContactModel
import com.example.proyecto_final_apps.data.local.entity.ContactWithUserModel

interface ContactRepository {

    suspend fun getContactsList(forceUpdate: Boolean): Resource<List<ContactWithUserModel>>
    suspend fun getContactData(
        userId: String,
        forceUpdate: Boolean
    ): Resource<ContactFullDataModel>
    suspend fun newContact(userId: String): Resource<ContactModel>
    suspend fun deleteContact(userId: String): Resource<Boolean>
}