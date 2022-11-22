package com.example.proyecto_final_apps.ui.fragments.FavoriteOperations

import androidx.datastore.preferences.protobuf.Empty
import androidx.lifecycle.ViewModel
import com.example.proyecto_final_apps.data.Resource
import com.example.proyecto_final_apps.data.local.entity.AccountModel
import com.example.proyecto_final_apps.data.local.entity.OperationModel
import com.example.proyecto_final_apps.data.repository.AccountRepository
import com.example.proyecto_final_apps.data.repository.OperationRepository
import com.example.proyecto_final_apps.domain.AccountDomain
import com.example.proyecto_final_apps.domain.OperationDomain
import com.example.proyecto_final_apps.ui.util.Status
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class FavoriteOperationsViewModel @Inject constructor(
    private val opDomain: OperationDomain,
    private val opRepository: OperationRepository
) : ViewModel() {

    private val _fragmentState: MutableStateFlow<Status<Boolean>> =
        MutableStateFlow(Status.Loading())
    val fragmentState: StateFlow<Status<Boolean>> = _fragmentState

    private val _selectedOperations: MutableStateFlow<MutableList<Int>> =
        MutableStateFlow(mutableListOf())
    val selectedOperations: StateFlow<MutableList<Int>> = _selectedOperations

    private val _favoriteOperationsList: MutableStateFlow<Status<List<OperationModel>>> =
        MutableStateFlow(Status.Default())
    val favoriteOperationsList: StateFlow<Status<List<OperationModel>>> = _favoriteOperationsList

    suspend fun getFavoriteOperations(forceUpdate: Boolean = false) {

        when (val operations = opDomain.getOperations(forceUpdate)) {
            is Resource.Success -> {
                val favoriteOperations = mutableListOf<OperationModel>()
                operations.data.forEach { operation ->
                    if (operation.favorite)
                        favoriteOperations.add(operation)
                }
                _favoriteOperationsList.value = Status.Success(favoriteOperations)

            }
            else -> {
                _favoriteOperationsList.value = Status.Error(operations.message ?: "")
            }
        }
    }

    fun setSuccessFragmentStatus(){
        if (_fragmentState.value !is Status.Error)
            _fragmentState.value = Status.Success(true)
    }

    fun addOperationSelected(operationId: Int){
        _selectedOperations.value.add(operationId)
    }

    fun removeOperationSelected(operationId: Int){
        _selectedOperations.value.remove(operationId)
    }

    suspend fun deleteSelected(): Flow<Status<Boolean>> {
        return flow{
            emit(Status.Loading())

            _selectedOperations.collectLatest{ selected ->
                selected.forEach { operationId ->
                    val resultDelete = opRepository.deleteOperation(operationId)

                    if(resultDelete is Resource.Error)
                        emit(Status.Error(resultDelete.message ?: ""))
                }
                emit(Status.Success(true))
            }
        }
    }

    fun desSelect(){
        _selectedOperations.value.removeAll(_selectedOperations.value)
    }

}