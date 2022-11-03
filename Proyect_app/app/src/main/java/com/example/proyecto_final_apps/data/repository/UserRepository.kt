package com.example.proyecto_final_apps.data.repository

import com.example.proyecto_final_apps.data.Resource
import com.example.proyecto_final_apps.data.local.entity.UserModel

interface UserRepository {

    suspend fun login(user:String, password:String):Resource<Boolean>
    suspend fun logout()
    suspend fun getUserData(): Resource<UserModel>
    suspend fun signUp(firstName: String, lastName: String, birthDate: String, user: String, password: String): Resource<Boolean>
    suspend fun getUserData(remote:Boolean): Resource<UserModel>
}