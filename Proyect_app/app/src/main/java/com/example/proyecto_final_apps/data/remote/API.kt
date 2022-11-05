package com.example.proyecto_final_apps.data.remote

import com.example.proyecto_final_apps.data.remote.dto.UserDto
import com.example.proyecto_final_apps.data.remote.dto.accountListResponse.AccountListDto
import com.example.proyecto_final_apps.data.remote.dto.loginResponse.LoginResponse
import com.example.proyecto_final_apps.data.remote.dto.operationResponse.OperationDto
import com.example.proyecto_final_apps.data.remote.dto.requests.LoginRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface API {

    @POST("/user/login")
    suspend fun login(
        @Body body: LoginRequest
    ): Response<LoginResponse>

    @GET("/user/sessionUserData")
    suspend fun getSessionUserData(
        @Header("authorization") token:String
    ): Response<UserDto>


    @GET("/operation/getAll")
    suspend fun getAllOperations(
        @Header("authorization") token:String
    ):Response<OperationDto>

    @GET("/account/list")
    suspend fun getAccountList(
        @Header("authorization") token:String
    ):Response<AccountListDto>
}