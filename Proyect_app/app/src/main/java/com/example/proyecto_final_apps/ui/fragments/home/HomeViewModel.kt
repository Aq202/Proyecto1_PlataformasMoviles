package com.example.proyecto_final_apps.ui.fragments.home

import androidx.lifecycle.ViewModel
import com.example.proyecto_final_apps.data.Resource
import com.example.proyecto_final_apps.data.local.entity.AccountModel
import com.example.proyecto_final_apps.data.local.entity.DebtAcceptedModel
import com.example.proyecto_final_apps.data.local.entity.OperationModel
import com.example.proyecto_final_apps.data.local.entity.UserModel
import com.example.proyecto_final_apps.data.repository.OperationRepository
import com.example.proyecto_final_apps.domain.DebtDomain
import com.example.proyecto_final_apps.domain.OperationDomain
import com.example.proyecto_final_apps.ui.util.Status
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject


@HiltViewModel
class HomeViewModel @Inject constructor(
    private val opRepository: OperationRepository,
    private val opDomain: OperationDomain,
    private val debtDomain: DebtDomain
) : ViewModel() {


    private val _fragmentState: MutableStateFlow<Status<Boolean>> =
        MutableStateFlow(Status.Loading())
    val fragmentState: StateFlow<Status<Boolean>> = _fragmentState

    private val _generalBalance: MutableStateFlow<Status<Double>> =
        MutableStateFlow(Status.Default())
    val generalBalance: StateFlow<Status<Double>> = _generalBalance

    private val _recentOperations: MutableStateFlow<Status<List<OperationModel>>> =
        MutableStateFlow(Status.Default())
    val recentOperations: StateFlow<Status<List<OperationModel>>> = _recentOperations

    private val _acceptedDebts: MutableStateFlow<Status<List<Pair<DebtAcceptedModel, UserModel>>>> =
        MutableStateFlow(Status.Default())
    val acceptedDebts: StateFlow<Status<List<Pair<DebtAcceptedModel, UserModel>>>> = _acceptedDebts


    suspend fun getGeneralBalance() {
        when (val result = opRepository.getGeneralBalance()) {
            is Resource.Success -> {
                println("Diego: " + result.data.toString())
                _generalBalance.value = Status.Success(result.data)
            }
            else -> {
                _generalBalance.value = Status.Error(result.message ?: "")
            }
        }

    }

    suspend fun getRecentOperations(forceUpdate: Boolean) {

        when (val result = opDomain.getOperations(forceUpdate)) {
            is Resource.Success -> {
                _recentOperations.value = Status.Success(result.data.take(3))
            }
            else -> {
                _recentOperations.value = Status.Error(result.message ?: "")
            }
        }
    }

    suspend fun getAcceptedDebts(forceUpdate: Boolean) {
        val result = debtDomain.getDebtList(forceUpdate)

        if (result is Resource.Success) {
            _acceptedDebts.value = Status.Success(result.data)
        } else {
            _acceptedDebts.value = Status.Error(result.message ?: "Ocurrió un error.")
        }
    }

    fun setSuccessFragmentStatus() {
        _fragmentState.value = Status.Success(true)
    }


}