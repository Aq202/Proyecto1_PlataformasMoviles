package com.example.proyecto_final_apps.data.remote.dto.requests

data class UpdateAccountRequest(
    val title: String? = null,
    val total: Double? = null,
    val defaultAccount: Boolean? = null,
)