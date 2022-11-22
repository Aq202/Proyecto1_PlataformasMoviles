package com.example.proyecto_final_apps.data.remote.dto.editProfileResponse

import com.example.proyecto_final_apps.data.remote.dto.UserDto

data class EditProfileResponse(
    val token: String,
    val userData: UserDto
)