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
    val localId: Int? = null,
    val account: String,
    val active: Boolean,
    val amount: Double,
    val category: Int,
    val favorite: Boolean,
    val date: String,
    val remoteId: String,
    val subject: String,
    val title: String,
    val requiresUpdate: Boolean? = false,
    val imgUrl: String?
)

fun OperationModel.getCategory(context: Context):CategoryModel?{
    return Category(context).getCategory(this.category);
}