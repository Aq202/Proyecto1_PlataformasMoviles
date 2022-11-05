package com.example.proyecto_final_apps.data.repository

import com.example.proyecto_final_apps.data.Resource
import com.example.proyecto_final_apps.data.local.entity.OperationModel

interface OperationRepository {

    suspend fun getOperations(forceUpdate:Boolean = false):Resource<List<OperationModel>>
    suspend fun getGeneralBalance(): Resource<Double>
    suspend fun getBalanceMovement():Resource<Double>
}