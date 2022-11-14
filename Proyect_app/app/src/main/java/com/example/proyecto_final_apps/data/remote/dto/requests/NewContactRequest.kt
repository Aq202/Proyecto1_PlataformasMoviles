package com.example.proyecto_final_apps.data.remote.dto.requests

data class NewContactRequest(
    val targetUser: String,
    val localId: Int
)