package com.example.proyecto_final_apps.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class UserModel (
    @PrimaryKey
    val id: String,
    val alias: String,
    val name: String,
    val lastName: String,
    val birthDate: String,
    val email: String,
    val imageUrl: String,
    var deletionPending:Boolean = false,
    var requiresUpdate: Boolean? = false,
)