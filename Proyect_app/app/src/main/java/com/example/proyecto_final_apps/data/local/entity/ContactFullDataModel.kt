package com.example.proyecto_final_apps.data.local.entity

data class ContactFullDataModel(
    val contact: ContactModel,
    val userAsContactData: UserModel,
    val debtsAccepted: List<DebtAcceptedModel>?
)