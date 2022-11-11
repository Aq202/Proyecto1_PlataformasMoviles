package com.example.proyecto_final_apps.data.remote

import com.example.proyecto_final_apps.data.remote.dto.AccountDto
import com.example.proyecto_final_apps.data.remote.dto.UserDto
import com.example.proyecto_final_apps.data.remote.dto.accountListResponse.AccountListDto
import com.example.proyecto_final_apps.data.remote.dto.deleteAccountResponse.DeleteAccountDto
import com.example.proyecto_final_apps.data.remote.dto.getOperationsResponse.GetOperationsDto
import com.example.proyecto_final_apps.data.remote.dto.loginResponse.LoginResponse
import com.example.proyecto_final_apps.data.remote.dto.requests.LoginRequest
import com.example.proyecto_final_apps.data.remote.dto.requests.NewAccountRequest
import com.example.proyecto_final_apps.data.remote.dto.requests.UpdateAccountRequest
import com.example.proyecto_final_apps.data.remote.dto.requests.SignUpRequest
import com.example.proyecto_final_apps.data.remote.dto.signUpResponse.SignUpResponse
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

    @POST("/user/signUp")
    suspend fun signUp(
        @Body body: SignUpRequest
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


}