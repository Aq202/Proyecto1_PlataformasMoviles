package com.example.proyecto_final_apps.data.remote.dto.requests

data class UpdateOperationRequest(
    val account: String? = null,
    val active: Boolean? = null,
    val amount: Double? = null,
    val category: Int? = null,
    val favorite: Boolean? = null,
    val title: String? = null,
    val description: String? = null,
    val imgUrl:String? = null,
)