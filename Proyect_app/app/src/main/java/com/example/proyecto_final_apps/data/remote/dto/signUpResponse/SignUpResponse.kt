package com.example.proyecto_final_apps.data.remote.dto.signUpResponse

import com.example.proyecto_final_apps.data.remote.dto.UserDto

data class SignUpResponse(
    val token: String,
    val userData: UserDto
)