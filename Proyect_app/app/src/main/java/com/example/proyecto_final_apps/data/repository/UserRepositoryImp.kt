package com.example.proyecto_final_apps.data.repository

import android.content.Context
import com.example.proyecto_final_apps.data.Resource
import com.example.proyecto_final_apps.data.local.Database
import com.example.proyecto_final_apps.data.local.MyDataStore
import com.example.proyecto_final_apps.data.local.entity.UserModel
import com.example.proyecto_final_apps.data.remote.API
import com.example.proyecto_final_apps.data.remote.ErrorParser
import com.example.proyecto_final_apps.data.remote.dto.requests.LoginRequest
import com.example.proyecto_final_apps.data.remote.dto.toUserModel
import com.example.proyecto_final_apps.helpers.Internet
import com.example.proyecto_final_apps.helpers.createPartFromString
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.ResponseBody
import retrofit2.Converter
import java.io.File
import javax.inject.Inject


class UserRepositoryImp @Inject constructor(
    val api: API,
    val context: Context,
    val database: Database,
    private val errorParser: ErrorParser
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
        } catch (ex: Exception) {
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
        email: String,
        password: String,
        profilePicPath: String
    ): Resource<Boolean> {

        try {


            //crear estructura a enviar como formData
            val dataMap: MutableMap<String, RequestBody> = mutableMapOf()
            dataMap["name"] = firstName.createPartFromString()
            dataMap["lastName"] = lastName.createPartFromString()
            dataMap["email"] = email.createPartFromString()
            dataMap["birthDate"] = birthDate.createPartFromString()
            dataMap["alias"] = user.createPartFromString()
            dataMap["password"] = password.createPartFromString()

            //Imagen en formato multipart

            val file = File(profilePicPath)
            val requestFile = file.asRequestBody("image/*".toMediaType())
            val multipartImage: MultipartBody.Part =
                MultipartBody.Part.createFormData("image", file.name, requestFile);


            val result = api.signUp(dataMap, multipartImage)

            return if (result.isSuccessful) {

                val response = result.body()

                val ds = MyDataStore(context)
                ds.saveKeyValue("token", response!!.token)

                //guardar datos del usuario
                database.userDao().insertUser(response.userData.toUserModel())

                return Resource.Success(true)

            } else {

                val errorBody = result.errorBody()
                val error = errorParser.parseErrorObject(errorBody)

                if(error != null) Resource.Error(error.err ?: "Ocurrió un error")
                else Resource.Error("Ocurrió un error.")


            }

        } catch (exception: Exception) {
            println(exception.message)
            return Resource.Error("Error de conexión al servidor.")
        }

    }

}