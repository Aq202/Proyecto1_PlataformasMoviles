package com.example.proyecto_final_apps.data.remote.dto.requests

data class SignUpRequest(
    val firstName: String,
    val lastName: String,
    val birthDate: String,
    val user: String,
    val password: String,
)