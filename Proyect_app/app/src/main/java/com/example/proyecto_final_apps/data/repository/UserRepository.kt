package com.example.proyecto_final_apps.data.repository

import com.example.proyecto_final_apps.data.Resource
import com.example.proyecto_final_apps.data.local.entity.UserModel
import okhttp3.MultipartBody

interface UserRepository {

    suspend fun login(user:String, password:String):Resource<Boolean>
    suspend fun logout()
    suspend fun signUp(firstName: String, lastName: String, birthDate: String, user: String, email: String, password: String, profilePic: MultipartBody.Part): Resource<Boolean>
    suspend fun getUserData(remote:Boolean): Resource<UserModel>
}