package com.example.proyecto_final_apps.data.repository

import com.example.proyecto_final_apps.data.Resource
import com.example.proyecto_final_apps.data.local.entity.ContactModel

interface ContactRepository {

    suspend fun getContactsList(forceUpdate:Boolean):Resource<List<ContactModel>>
}