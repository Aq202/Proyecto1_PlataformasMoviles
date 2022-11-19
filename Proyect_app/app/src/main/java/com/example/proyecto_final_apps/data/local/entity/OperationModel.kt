package com.example.proyecto_final_apps.data.local.entity

import android.content.Context
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.proyecto_final_apps.data.Category
import com.example.proyecto_final_apps.data.CategoryModel
import com.example.proyecto_final_apps.ui.adapters.OperationItem
import java.util.*

@Entity
data class OperationModel(
    @PrimaryKey(autoGenerate = true)
    var localId: Int? = null,
    var accountRemoteId: String?,
    var accountLocalId:Int,
    var active: Boolean,
    var amount: Double,
    var category: Int,
    var favorite: Boolean,
    var date: String,
    val remoteId: String? = null,
    val subject: String? = null,
    var title: String,
    var description: String?,
    var requiresUpdate: Boolean? = false,
    var imgUrl: String?,
    var deletionPending:Boolean = false
)

fun OperationModel.getCategory(context: Context):CategoryModel?{
    return Category(context).getCategory(this.category);
}

fun OperationModel.toOperationItem(context: Context):OperationItem{
    return OperationItem(
        localId = localId!!,
        remoteId = remoteId,
        title = title,
        category= getCategory(context),
        amount = amount,
        active = active,
        imgUrl = imgUrl
    )
}