package com.example.proyecto_final_apps.data.remote.dto.operationDto

import com.example.proyecto_final_apps.data.local.entity.OperationModel
import com.example.proyecto_final_apps.data.remote.dto.AccountDto
import com.example.proyecto_final_apps.helpers.apiUrl

data class OperationDto(
    val account: AccountDto,
    val active: Boolean,
    val amount: Double,
    val category: Int,
    val date: String,
    val favorite: Boolean,
    val formattedDate: String,
    val id: String,
    val localId: Int,
    val subject: String,
    val title: String,
    val description: String?,
    val imgUrl:String?
)

fun OperationDto.toOperationModel(): OperationModel {

    return OperationModel(

        localId = this.localId,
        accountLocalId = this.account.localId,
        accountRemoteId = this.account.id,
        active = this.active,
        amount = this.amount,
        category = this.category,
        favorite = this.favorite,
        date = formattedDate,
        remoteId = this.id,
        subject = this.subject,
        title = this.title,
        description = this.description,
        requiresUpdate = false,
        imgUrl = if(this.imgUrl != null) (apiUrl + this.imgUrl) else null
    )
}