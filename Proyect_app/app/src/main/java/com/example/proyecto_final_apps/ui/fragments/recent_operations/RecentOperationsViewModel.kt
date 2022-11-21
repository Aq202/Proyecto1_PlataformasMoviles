package com.example.proyecto_final_apps.ui.fragments.recent_operations

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyecto_final_apps.data.Resource
import com.example.proyecto_final_apps.data.local.entity.OperationModel
import com.example.proyecto_final_apps.domain.OperationDomain
import com.example.proyecto_final_apps.ui.util.Status
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecentOperationsViewModel @Inject constructor(private val operationDomain: OperationDomain) :ViewModel() {

    private val _recentOperations = MutableStateFlow<Status<List<OperationModel>>>(Status.Default())
    val recentOperations:StateFlow<Status<List<OperationModel>>> = _recentOperations

    fun getRecentOperations(forceUpdate:Boolean){

        _recentOperations.value = Status.Loading()

        viewModelScope.launch {

            val result = operationDomain.getOperations(forceUpdate)
            if(result is Resource.Success) _recentOperations.value = Status.Success(result.data)
            else _recentOperations.value = Status.Error(result.message ?: "No se obtuvieron resultados.")
        }
    }
}