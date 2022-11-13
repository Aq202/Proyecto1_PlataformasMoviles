package com.example.proyecto_final_apps.data.remote.dto.requests

data class NewOperationRequest(
    val account: String,
    val active: Boolean,
    val amount: Double,
    val category: Int,
    val date: String,
    val favourite: Boolean,
    val localId: Int,
    val title: String,
    val description: String?,
    val imgUrl:String?
)