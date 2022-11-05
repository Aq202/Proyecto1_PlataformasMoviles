package com.example.proyecto_final_apps.ui.fragments.home

import androidx.lifecycle.ViewModel
import com.example.proyecto_final_apps.data.Resource
import com.example.proyecto_final_apps.data.local.entity.OperationModel
import com.example.proyecto_final_apps.data.repository.OperationRepository
import com.example.proyecto_final_apps.ui.util.Status
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject



@HiltViewModel
class HomeViewModel @Inject constructor(
    private val opRepository :OperationRepository
) : ViewModel() {


    private val _generalBalance:MutableStateFlow<Status<Double>> = MutableStateFlow(Status.Default())
    val generalBalance:StateFlow<Status<Double>> = _generalBalance

    private val _recentOperations:MutableStateFlow<Status<List<OperationModel>>> = MutableStateFlow(Status.Default())
    val recentOperations:StateFlow<Status<List<OperationModel>>> = _recentOperations

    private val _pendingOperations:MutableStateFlow<Status<List<OperationModel>>> = MutableStateFlow(Status.Default())
    val pendingOperations:StateFlow<Status<List<OperationModel>>> = _pendingOperations

    suspend fun getGeneralBalance(){
            when(val result = opRepository.getGeneralBalance()){
                is Resource.Success-> {
                    println("Diego: "+result.data.toString())
                    _generalBalance.value = Status.Success(result.data)
                }
                else -> {
                    _generalBalance.value = Status.Error(result.message ?: "")
                }
            }

    }

    suspend fun getRecentOperations(forceUpdate:Boolean){

        when(val result = opRepository.getOperations(forceUpdate)){
            is Resource.Success -> {
                _recentOperations.value = Status.Success(result.data.take(3))
            }
            else -> {
                _recentOperations.value = Status.Error(result.message ?: "")
            }
        }
    }




}