package com.example.proyecto_final_apps.ui.fragments.newOperation

import androidx.lifecycle.ViewModel
import com.example.proyecto_final_apps.data.Resource
import com.example.proyecto_final_apps.data.local.entity.OperationModel
import com.example.proyecto_final_apps.data.repository.OperationRepository
import com.example.proyecto_final_apps.helpers.DateParse.getDayValue
import com.example.proyecto_final_apps.helpers.DateParse.getMonthValue
import com.example.proyecto_final_apps.helpers.DateParse.getYearValue
import com.example.proyecto_final_apps.ui.util.Status
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import java.util.*
import javax.inject.Inject

@HiltViewModel
class NewOperationViewModel @Inject constructor(private val opRepository: OperationRepository) :
    ViewModel() {

    fun createOperation(
        title: String,
        accountRemoteId: String,
        accountLocalId: Int,
        amount: Double,
        active: Boolean,
        description: String?,
        category: Int,
        favorite: Boolean,
    ): Flow<Status<OperationModel>> {
        val currentDate = Date()

        return flow {

            emit(Status.Loading())

            val result = opRepository.createOperation(
                title = title,
                accountRemoteId = accountRemoteId,
                accountLocalId = accountLocalId,
                amount = amount,
                active = active,
                description = description,
                category = category,
                favorite = favorite,
                date = "${currentDate.getMonthValue()}/${currentDate.getDayValue()}/${currentDate.getYearValue()}"
            )

            if(result is Resource.Success)
                emit(Status.Success(result.data))
            else emit(Status.Error(result.message ?: ""))
        }
    }

}