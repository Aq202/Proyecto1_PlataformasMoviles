package com.example.proyecto_final_apps.data.remote.dto.getContactDataResponse

import com.example.proyecto_final_apps.data.local.entity.ContactModel
import com.example.proyecto_final_apps.data.remote.dto.DebtsAcceptedDto
import com.example.proyecto_final_apps.data.remote.dto.UserDto

data class GetContactDataDto(
    val debtsAccepted: List<DebtsAcceptedDto>?,
    val debtsToAccept: List<Any>?,
    val id: String,
    val localId: Int,
    val subject: String,
    val userAsContact: UserDto
)

fun GetContactDataDto.toContactModel():ContactModel{
    return ContactModel(localId=localId, remoteId = id, userAsContact = userAsContact.id )
}