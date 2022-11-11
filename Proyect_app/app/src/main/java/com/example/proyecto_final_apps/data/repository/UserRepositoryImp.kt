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
                ds.saveKeyValue("userId", response.userData.id)

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
    override suspend fun getUserInSessionData(remote: Boolean): Resource<UserModel> {

        val ds = MyDataStore(context)
        val token = ds.getValueFromKey("token") ?: return Resource.Error("No token")
        val userId = ds.getValueFromKey("userId") ?: return Resource.Error("No userId")


        //get from local database
        val storedData = database.userDao().getUser(userId)

        if (storedData != null && (!remote || !Internet.checkForInternet(context)))
            return Resource.Success(storedData)

        //Si no hay datos locales o se requiere descargar datos remotos

        //get data from api
        try {
            val result = api.getSessionUserData(token)

            if (result.isSuccessful) {
                val userData = result.body()?.toUserModel()
                if (userData != null) {
                    database.userDao().insertUser(userData)

                    return Resource.Success(userData)
                }
            }
        } catch (ex: Exception) {
            println("Error de conexión.")
        }

        return Resource.Error("No se obtuvieron datos locales ni remotos")
    }

    override suspend fun getUserData(id: String, forceUpdate: Boolean): Resource<UserModel> {

        val user = database.userDao().getUser(id)

        if (user != null && !(forceUpdate && Internet.checkForInternet(context))) return Resource.Success(
            user
        ) //datos locales

        try {
            //search in api
            val ds = MyDataStore(context)
            val token = ds.getValueFromKey("token") ?: return Resource.Error("No token")

            val userRequestResult = api.getUserData(token, id)

            if (userRequestResult.isSuccessful) {
                val userData = userRequestResult.body()?.toUserModel()
                    ?: return Resource.Error("No se obtuvieron resultados de la api.")

                //guardar en la base de datos
                database.userDao().insertUser(userData)
                return Resource.Success(userData)

            }

        } catch (ex: Exception) {
            println("Diego: ${ex.message}")
        }
        return Resource.Error("Ocurrió un error.")

    }

    override suspend fun searchUsers(query: String): Resource<Pair<List<UserModel>?, List<UserModel>?>> {

        val ds = MyDataStore(context)

        if (Internet.checkForInternet(context)) {

            try {
                //Buscar datos en api. Los datos no se guardan localmente.
                val token = ds.getValueFromKey("token") ?: return Resource.Error("No token")

                val response = api.searchUsers(token, query)

                if (response.isSuccessful) {
                    response.body()?.let { usersList ->
                        val contacts = mutableListOf<UserModel>()
                        val externalUsers = mutableListOf<UserModel>()

                        usersList.map { user ->
                            val userModel = user.toUserModel()
                            //verificar si es contacto o usuario externo
                            val contactResult =
                                database.contactDao().getContactByUserAsContact(userModel.id)

                            if (contactResult == null) externalUsers.add(userModel)
                            else contacts.add(userModel)
                        }

                        if (contacts.isEmpty() && externalUsers.isEmpty()) return Resource.Error("No se encontraron coincidencias.")
                        else return Resource.Success(
                            Pair(
                                contacts.ifEmpty { null },
                                externalUsers.ifEmpty { null }
                            )
                        )
                    }

                }

            } catch (ex: Exception) {
                println("Diego: ${ex.message}")
            }

        }

        //Buscar solo en usuarios guardados
        val userId = ds.getValueFromKey("userId") ?: return Resource.Error("No userId")

        val usersList = database.userDao().getAllUsers(userId)

        if (usersList.isEmpty()) return Resource.Error("No se encontraron coincidencias.")
        else {

            //filtrar por nombre, apellido o alias
            val regex = query.toRegex(RegexOption.IGNORE_CASE)
            val filteredUsers = usersList.filter {
                regex.containsMatchIn(it.name) || regex.containsMatchIn(it.lastName) || regex.containsMatchIn(
                    it.alias
                )
            }

            return if (filteredUsers.isEmpty()) Resource.Error("No se encontraron coincidencias.")
            else Resource.Success(Pair(filteredUsers, null)) //retornar usuarios locales

        }

    }

    override suspend fun logout() {
        val ds = MyDataStore(context)

        ds.removeKey("token")
        database.userDao().deleteAll()
        database.operationDao().deleteAllOperations()
        database.accountDao().deleteAll()
        database.contactDao().deleteAll()
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
                ds.saveKeyValue("userId", response.userData.id)

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