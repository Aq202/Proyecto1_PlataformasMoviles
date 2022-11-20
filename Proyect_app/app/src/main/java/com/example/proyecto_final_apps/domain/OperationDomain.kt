package com.example.proyecto_final_apps.domain

import com.example.proyecto_final_apps.data.Resource
import com.example.proyecto_final_apps.data.local.entity.OperationModel

interface OperationDomain {

    suspend fun getOperations(forceUpdate: Boolean): Resource<List<OperationModel>>
    suspend fun getOperationData(
        operationLocalId: Int,
        forceUpdate: Boolean
    ): Resource<OperationModel>

}