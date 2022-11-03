package com.example.proyecto_final_apps.data.repository

import android.content.Context
import com.example.proyecto_final_apps.data.Resource
import com.example.proyecto_final_apps.data.local.Database
import com.example.proyecto_final_apps.data.local.MyDataStore
import com.example.proyecto_final_apps.data.local.entity.UserModel
import com.example.proyecto_final_apps.data.remote.API
import com.example.proyecto_final_apps.data.remote.dto.UserDto
import com.example.proyecto_final_apps.data.remote.dto.requests.LoginRequest
import com.example.proyecto_final_apps.data.remote.dto.requests.SignUpRequest
import com.example.proyecto_final_apps.data.remote.dto.toUserModel
import com.example.proyecto_final_apps.helpers.Internet
import javax.inject.Inject

class UserRepositoryImp @Inject constructor(
    val api: API,
    val context: Context,
    val database: Database
) : UserRepository {


    override suspend fun login(user: String, password: String): Resource<Boolean> {

        try {
            val result = api.login(LoginRequest(user = user, password = password))

            return if (result.isSuccessful) {

                val response = result.body()
                val ds = MyDataStore(context)

                ds.saveKeyValue("token", response!!.token)

                //guardar datos del usuario
                database.userDao().deleteAll()
                database.userDao().insertUser(response.userData.toUserModel())

                return Resource.Success(true)
            } else Resource.Error("Usuario o contraseña incorrectos.")

        } catch (ex: Exception) {
            println(ex.message)
            return Resource.Error("Error de conexión al servidor.")
        }

    }

    override suspend fun getUserData(): Resource<UserModel> {

        if (Internet.checkForInternet(context)) {
            val ds = MyDataStore(context)
            val token = ds.getValueFromKey("token") ?: ""

            //get data from api
            val result = api.getSessionUserData(token)

            if (result.isSuccessful) {
                val userData = result.body()?.toUserModel()
                if (userData != null) {
                    database.userDao().deleteAll()
                    database.userDao().insertUser(userData)

                    return Resource.Success(userData)
                }
            }
        }

        val storedData = database.userDao().getUser()
            ?: return Resource.Error("No hay conexión ni datos locales.")

        return Resource.Success(storedData)
    }

    override suspend fun logout(){
        val ds = MyDataStore(context)

        ds.removeKey("token")
        database.userDao().deleteAll()
    }

    override suspend fun signUp(
        firstName: String,
        lastName: String,
        birthDate: String,
        user: String,
        password: String
    ): Resource<Boolean> {

        try{

            val result = api.signUp(SignUpRequest(
                firstName = firstName,
                lastName = lastName,
                birthDate = birthDate,
                user = user,
                password = password
            ))

            return if (result.isSuccessful){

                login(user, password)
            } else{
                Resource.Error("El correo electrónico o nombre de usuario ya está registrado")
            }

        }catch (exception: Exception){
            println(exception.message)
            return Resource.Error("Error de conexión al servidor.")
        }

    }

}