package com.example.proyecto_final_apps.data.remote.dto

import com.example.proyecto_final_apps.data.local.entity.UserModel
import com.example.proyecto_final_apps.helpers.apiUrl

data class UserDto(
    val alias: String,
    val birthDate: String,
    val email: String,
    val id: String,
    val imageUrl: String,
    val lastName: String,
    val name: String
)

fun UserDto.toUserModel(): UserModel {
    return UserModel(
    alias = this.alias,
    name = this.name,
    lastName =  this.lastName,
    birthDate = this.birthDate,
    email = this.email,
    id = this.id,
    imageUrl = apiUrl + this.imageUrl,
    )
}