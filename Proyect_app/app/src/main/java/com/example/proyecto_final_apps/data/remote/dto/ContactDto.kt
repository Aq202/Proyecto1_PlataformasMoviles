package com.example.proyecto_final_apps.data.remote.dto

import com.example.proyecto_final_apps.data.local.entity.ContactModel

data class ContactDto(
    val debtsAccepted: List<String>,
    val debtsToAccept: List<String>,
    val id: String,
    val localId: Int,
    val subject: String,
    val userAsContact: UserDto
)

fun ContactDto.toContactModel(): ContactModel {
    return ContactModel(localId = localId, remoteId = id, userAsContact = userAsContact.id)
}