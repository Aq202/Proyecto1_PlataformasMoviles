package com.example.proyecto_final_apps.data.local.entity

data class DebtWithContactModel(
    val contact: UserModel,
    val acceptedDebt: DebtAcceptedModel
)