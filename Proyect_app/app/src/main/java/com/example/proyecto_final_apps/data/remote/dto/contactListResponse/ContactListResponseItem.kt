package com.example.proyecto_final_apps.data.remote.dto.contactListResponse

import com.example.proyecto_final_apps.data.local.entity.ContactModel
import com.example.proyecto_final_apps.data.remote.dto.UserDto

data class ContactListResponseItem(
    val debtsAccepted: List<String>,
    val debtsToAccept: List<String>,
    val id: String,
    val localId: Int,
    val subject: String,
    val userAsContact: UserDto
)

fun ContactListResponseItem.toContactModel(): ContactModel {
    return ContactModel(localId = localId, remoteId = id, userAsContact = userAsContact.id)
}