package com.example.proyecto_final_apps.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ContactModel(
    @PrimaryKey(autoGenerate = true)
    var localId: Int? = null,
    var remoteId: String?,
    val userAsContact:String,
    var deletionPending:Boolean = false,
    var requiresUpdate: Boolean? = false,
    )


data class ContactWithUserModel(
    val contact:ContactModel,
    val user:UserModel
)