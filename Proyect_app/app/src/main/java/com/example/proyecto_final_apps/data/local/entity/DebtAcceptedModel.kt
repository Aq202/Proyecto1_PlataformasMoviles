package com.example.proyecto_final_apps.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class DebtAcceptedModel(
    @PrimaryKey(autoGenerate = true)
    val localId: Int? = null,
    val accountInvolved: String,
    val active: Boolean,
    val amount: Double,
    val remoteId: String?,
    val userInvolved: String,
    var deletionPending: Boolean = false,
    var requiresUpdate: Boolean = false,
)

