package com.example.proyecto_final_apps.data.remote.dto.loginResponse

import com.example.proyecto_final_apps.data.remote.dto.UserDto


data class LoginResponse(
    val token: String,
    val userData: UserDto
)