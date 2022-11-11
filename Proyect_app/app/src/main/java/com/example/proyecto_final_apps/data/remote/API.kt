package com.example.proyecto_final_apps.data.remote

import com.example.proyecto_final_apps.data.remote.dto.AccountDto
import com.example.proyecto_final_apps.data.remote.dto.UserDto
import com.example.proyecto_final_apps.data.remote.dto.accountListResponse.AccountListDto
import com.example.proyecto_final_apps.data.remote.dto.contactListResponse.ContactListResponseDto
import com.example.proyecto_final_apps.data.remote.dto.deleteAccountResponse.DeleteAccountDto
import com.example.proyecto_final_apps.data.remote.dto.getOperationsResponse.GetOperationsDto
import com.example.proyecto_final_apps.data.remote.dto.loginResponse.LoginResponse
import com.example.proyecto_final_apps.data.remote.dto.requests.LoginRequest
import com.example.proyecto_final_apps.data.remote.dto.requests.NewAccountRequest
import com.example.proyecto_final_apps.data.remote.dto.requests.UpdateAccountRequest
import com.example.proyecto_final_apps.data.remote.dto.requests.SignUpRequest
import com.example.proyecto_final_apps.data.remote.dto.searchUsersResponse.SearchUsersDto
import com.example.proyecto_final_apps.data.remote.dto.signUpResponse.SignUpResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface API {

    @POST("/user/login")
    suspend fun login(
        @Body body: LoginRequest
    ): Response<LoginResponse>

    @GET("/user/sessionUserData")
    suspend fun getSessionUserData(
        @Header("authorization") token:String
    ): Response<UserDto>

    @Multipart
    @POST("/user/register")
    suspend fun signUp(
        @PartMap() data:MutableMap<String, RequestBody>,
        @Part image: MultipartBody.Part
    ): Response<SignUpResponse>


    @GET("/operation/getAll")
    suspend fun getAllOperations(
        @Header("authorization") token:String
    ):Response<GetOperationsDto>

    @GET("/account/list")
    suspend fun getAccountList(
        @Header("authorization") token:String
    ):Response<AccountListDto>

    @POST("/account/{accountId}/setAsDefault")
    suspend fun setAsDefaultAccount(
        @Header("authorization") token:String,
        @Path("accountId") accountId:String
    ):Response<Void>

    @DELETE("/account/{accountId}")
    suspend fun deleteAccount(
        @Header("authorization") token:String,
        @Path("accountId") accountId:String
    ):Response<DeleteAccountDto>

    @POST("/account/create")
    suspend fun createAccount(
        @Header("authorization") token:String,
        @Body body: NewAccountRequest
    ): Response<AccountDto>

    @POST("/account/update/{accountId}")
    suspend fun updateAccount(
        @Header("authorization") token:String,
        @Path("accountId") accountId:String,
        @Body body: UpdateAccountRequest
    ): Response<AccountDto>

    @GET("/user/contactsList")
    suspend fun getContactsList(
        @Header("authorization") token:String
    ):Response<ContactListResponseDto>

    @GET("/user/data/{userId}")
    suspend fun getUserData(
        @Header("authorization") token:String,
        @Path("userId") accountId:String,
    ):Response<UserDto>


    @GET("/user/search")
    suspend fun searchUsers(
        @Header("authorization") token:String,
        @Query("query") query:String
    ):Response<SearchUsersDto>

}