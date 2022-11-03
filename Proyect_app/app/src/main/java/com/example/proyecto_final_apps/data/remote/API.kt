package com.example.proyecto_final_apps.data.remote

import com.example.proyecto_final_apps.data.remote.dto.UserDto
import com.example.proyecto_final_apps.data.remote.dto.loginResponse.LoginResponse
import com.example.proyecto_final_apps.data.remote.dto.requests.LoginRequest
import com.example.proyecto_final_apps.data.remote.dto.requests.SignUpRequest
import com.example.proyecto_final_apps.data.remote.dto.signUpResponse.SignUpResponse
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

    @POST("/user/signUp")
    suspend fun signUp(
        @Body body: SignUpRequest
    ): Response<SignUpResponse>




}