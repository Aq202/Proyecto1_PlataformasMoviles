package com.example.proyecto_final_apps.data.remote.dto.requests

import okhttp3.MultipartBody

data class SignUpRequest(
    val firstName: String,
    val lastName: String,
    val birthDate: String,
    val user: String,
    val password: String,
    val email: String,
    val profilePic: MultipartBody.Part
)