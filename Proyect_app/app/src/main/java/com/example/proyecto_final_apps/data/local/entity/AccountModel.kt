package com.example.proyecto_final_apps.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class AccountModel(
    @PrimaryKey(autoGenerate = true)
    val localId: Int? = null,
    val remoteId: String,
    val allowNegativeValues: Boolean,
    var defaultAccount: Boolean,
    val editable: Boolean,
    val subject: String,
    val title: String,
    val total: Double,
    var deletionPending:Boolean = false,
    var requiresUpdate: Boolean? = false,
)