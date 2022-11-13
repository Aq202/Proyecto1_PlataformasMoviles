package com.example.proyecto_final_apps.data.repository

import com.example.proyecto_final_apps.data.Resource
import com.example.proyecto_final_apps.data.local.entity.OperationModel
import java.util.*

interface OperationRepository {

    suspend fun getOperations(forceUpdate:Boolean = false):Resource<List<OperationModel>>
    suspend fun getGeneralBalance(): Resource<Double>
    suspend fun getBalanceMovement():Resource<Double>
    suspend fun getAccountOperations(localAccountId:Int, forceUpdate: Boolean, startDate: Date?, endDate:Date?):Resource<List<OperationModel>>
    suspend fun uploadPendingChanges()
    suspend fun createOperation(title: String, accountRemoteId: String, accountLocalId: Int, amount: Double, active: Boolean, description: String?, category: Int, favorite: Boolean, date: String, imgUrl:String? = null):Resource<OperationModel>
}