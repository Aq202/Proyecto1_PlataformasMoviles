package com.example.proyecto_final_apps.data.remote.dto.getOperationsResponse

import com.example.proyecto_final_apps.data.remote.dto.operationDto.OperationDto

data class GetOperationsDto(
    val operations: List<OperationDto>
)