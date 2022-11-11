package com.example.proyecto_final_apps.data.remote.dto.requests

data class NewAccountRequest(
    val localId: Int,
    val title: String,
    val total: Double,
    val defaultAccount: Boolean,
    )