package com.example.proyecto_final_apps.data.remote.dto.requests

data class NewDebtRequest(

    val localId: Int,
    val contactId: String,
    val active: Boolean,
    val amount: Double,
    val account: String,
    val description:String?

    )