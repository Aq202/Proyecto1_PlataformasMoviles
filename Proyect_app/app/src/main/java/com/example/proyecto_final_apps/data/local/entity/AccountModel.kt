package com.example.proyecto_final_apps.data.local.entity

import androidx.room.Database
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class AccountModel(
    @PrimaryKey(autoGenerate = true)
    var localId: Int? = null,
    val remoteId: String? = null,
    val allowNegativeValues: Boolean = false,
    var defaultAccount: Boolean,
    val editable: Boolean = true,
    val subject: String? = null,
    var title: String,
    var total: Double = 0.0,
    var deletionPending:Boolean = false,
    var requiresUpdate: Boolean? = false,
)

