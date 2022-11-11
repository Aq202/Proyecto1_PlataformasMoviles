package com.example.proyecto_final_apps.data.repository

import com.example.proyecto_final_apps.data.Resource
import com.example.proyecto_final_apps.data.local.entity.UserModel

interface UserRepository {

    suspend fun login(user:String, password:String):Resource<Boolean>
    suspend fun logout()
    suspend fun signUp(firstName: String, lastName: String, birthDate: String, user: String, email: String, password: String): Resource<Boolean>
    suspend fun getUserInSessionData(remote:Boolean): Resource<UserModel>
    suspend fun getUserData(id:String, forceUpdate:Boolean): Resource<UserModel>
    suspend fun searchUsers(query:String):Resource<Pair<List<UserModel>?,List<UserModel>?>>
}