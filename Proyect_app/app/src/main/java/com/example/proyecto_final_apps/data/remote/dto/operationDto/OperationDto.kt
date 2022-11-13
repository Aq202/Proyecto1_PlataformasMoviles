package com.example.proyecto_final_apps.data.remote.dto.operationDto

import com.example.proyecto_final_apps.data.local.entity.OperationModel
import com.example.proyecto_final_apps.data.remote.dto.AccountDto

data class Operation(
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
    val imgUrl:String?
)

fun Operation.toOperationModel(): OperationModel {

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
        requiresUpdate = false,
        imgUrl = this.imgUrl
    )
}