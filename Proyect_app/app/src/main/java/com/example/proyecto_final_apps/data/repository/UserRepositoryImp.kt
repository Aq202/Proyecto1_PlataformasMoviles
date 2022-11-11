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
import okhttp3.MultipartBody
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

    /**
     * Se encarga de obtener los datos del usuario en sesión.
     * Retorna Success si se obtienen los datos o Error si no.
     */
    override suspend fun getUserData(remote: Boolean): Resource<UserModel> {

        val ds = MyDataStore(context)
        val token = ds.getValueFromKey("token") ?: return Resource.Error("No token")

        //get from local database
        val storedData = database.userDao().getUser()

        if (storedData != null && (!remote || !Internet.checkForInternet(context)))
            return Resource.Success(storedData)

        //Si no hay datos locales o se requiere descargar datos remotos

        //get data from api
        try {
            val result = api.getSessionUserData(token)

            if (result.isSuccessful) {
                val userData = result.body()?.toUserModel()
                if (userData != null) {
                    database.userDao().deleteAll()
                    database.userDao().insertUser(userData)

                    return Resource.Success(userData)
                }
            }
        }catch(ex:Exception){
            println("Error de conexión.")
        }

        return Resource.Error("No se obtuvieron datos locales ni remotos")
}

override suspend fun logout() {
    val ds = MyDataStore(context)

    ds.removeKey("token")
    database.userDao().deleteAll()
    database.operationDao().deleteAllOperations()
    database.accountDao().deleteAll()
}

    override suspend fun signUp(
        firstName: String,
        lastName: String,
        birthDate: String,
        user: String,
        password: String,
        email: String,
        profilePic: MultipartBody.Part
    ): Resource<Boolean> {

        try{

            val result = api.signUp(SignUpRequest(
                firstName = firstName,
                lastName = lastName,
                birthDate = birthDate,
                user = user,
                email = email,
                password = password,
                profilePic = profilePic

            ))

            return if (result.isSuccessful){

                val response = result.body()
                val ds = MyDataStore(context)

                ds.saveKeyValue("token", response!!.token)

                //guardar datos del usuario
                database.userDao().deleteAll()
                database.userDao().insertUser(response.userData.toUserModel())

                return Resource.Success(true)

            } else{
                Resource.Error("El correo electrónico o nombre de usuario ya está registrado.")
            }

        }catch (exception: Exception){
            println(exception.message)
            return Resource.Error("Error de conexión al servidor.")
        }

    }

}