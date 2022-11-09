package com.example.proyecto_final_apps.data.remote.dto.operationDto

data class Account(
    val __v: Int,
    val _id: String,
    val allowNegativeValues: Boolean,
    val defaultAccount: Boolean,
    val editable: Boolean,
    val localId: Int,
    val subject: String,
    val title: String,
    val total: Int
)