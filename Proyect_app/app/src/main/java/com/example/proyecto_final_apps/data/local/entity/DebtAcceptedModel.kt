package com.example.proyecto_final_apps.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class DebtAcceptedModel(
    @PrimaryKey(autoGenerate = true)
    var localId: Int? = null,
    val accountInvolved: Int,
    val active: Boolean,
    val amount: Double,
    var remoteId: String?,
    val description:String?,
    val userInvolved: String,
    var deletionPending: Boolean = false,
    var requiresUpdate: Boolean = false,
)

