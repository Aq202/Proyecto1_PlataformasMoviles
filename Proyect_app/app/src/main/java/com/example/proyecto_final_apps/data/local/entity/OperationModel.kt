package com.example.proyecto_final_apps.data.local.entity

import android.content.Context
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.proyecto_final_apps.data.Category
import com.example.proyecto_final_apps.data.CategoryModel
import java.util.*

@Entity
data class OperationModel(
    @PrimaryKey(autoGenerate = true)
    var localId: Int? = null,
    val accountRemoteId: String?,
    val accountLocalId:Int,
    val active: Boolean,
    val amount: Double,
    val category: Int,
    val favorite: Boolean,
    val date: String,
    val remoteId: String? = null,
    val subject: String? = null,
    val title: String,
    val description: String?,
    var requiresUpdate: Boolean? = false,
    val imgUrl: String?,
    var deletionPending:Boolean = false
)

fun OperationModel.getCategory(context: Context):CategoryModel?{
    return Category(context).getCategory(this.category);
}