package com.example.proyecto_final_apps.domain

import com.example.proyecto_final_apps.data.Resource
import com.example.proyecto_final_apps.data.local.entity.ContactFullDataModel
import com.example.proyecto_final_apps.data.local.entity.ContactWithUserModel

interface ContactDomain {

    suspend fun getContactsList(forceUpdate: Boolean): Resource<List<ContactWithUserModel>>
    suspend fun getContactData(
        userId: String,
        forceUpdate: Boolean
    ): Resource<ContactFullDataModel>
}