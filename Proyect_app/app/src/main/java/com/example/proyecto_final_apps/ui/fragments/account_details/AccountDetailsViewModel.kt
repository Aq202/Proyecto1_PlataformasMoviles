package com.example.proyecto_final_apps.ui.fragments.account_details

import androidx.lifecycle.ViewModel
import com.example.proyecto_final_apps.data.Resource
import com.example.proyecto_final_apps.data.local.entity.AccountModel
import com.example.proyecto_final_apps.data.local.entity.OperationModel
import com.example.proyecto_final_apps.data.repository.AccountRepository
import com.example.proyecto_final_apps.data.repository.OperationRepository
import com.example.proyecto_final_apps.ui.util.Status
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import java.util.*
import javax.inject.Inject

@HiltViewModel
class AccountDetailsViewModel @Inject constructor(
    private val opRepository: OperationRepository,
    private val acRepository: AccountRepository

) : ViewModel() {

    sealed class Filter {
        class ActiveDateFilter(val startDate: Date, val endDate: Date) : Filter()
        object EmptyFilter : Filter()
    }

    private val _fragmentState: MutableStateFlow<Status<Boolean>> =
        MutableStateFlow(Status.Loading())
    val fragmentState: StateFlow<Status<Boolean>> = _fragmentState

    private var successesCount = 0;

    private val _dateFilterFlow: MutableStateFlow<Filter> = MutableStateFlow(Filter.EmptyFilter)
    val dateFilterFlow: MutableStateFlow<Filter> = _dateFilterFlow

    private val _initialAccountOperations: MutableStateFlow<Status<List<OperationModel>>> =
        MutableStateFlow(Status.Default())
    val initialAccountOperations: StateFlow<Status<List<OperationModel>>> =
        _initialAccountOperations

    private val _expensesSelected: MutableStateFlow<Status<Boolean>> =
        MutableStateFlow(Status.Default()) //true:gastos, false: ingresos
    val expensesSelected: StateFlow<Status<Boolean>> = _expensesSelected

    private val _accountOperations: MutableStateFlow<Status<List<OperationModel>>> =
        MutableStateFlow(Status.Default())
    val accountOperations: StateFlow<Status<List<OperationModel>>> =
        _accountOperations

    private val _accountBalanceData: MutableStateFlow<Status<Pair<Double, Double>>> =
        MutableStateFlow(Status.Default())
    val accountBalanceData: StateFlow<Status<Pair<Double, Double>>> = _accountBalanceData

    private val _accountData: MutableStateFlow<Status<AccountModel>> =
        MutableStateFlow(Status.Default())
    val accountData: StateFlow<Status<AccountModel>> = _accountData


    fun addDateFilter(start: Date, end: Date) {
        _dateFilterFlow.value = Filter.ActiveDateFilter(start, end)
    }

    fun removeDateFilter() {
        _dateFilterFlow.value = Filter.EmptyFilter
    }

    suspend fun getAccountExpenses(
        localAccountId: Int,
        forceUpdate: Boolean = false,
        autoChange: Boolean = false
    ) {

        when (val operationsResult =
            opRepository.getAccountOperations(localAccountId, forceUpdate, null, null)) {
            is Resource.Success -> {
                //obtener solo ingresos o gastos
                val expenses = operationsResult.data.filter {
                    val expensesSelectedState = _expensesSelected.value
                    if (expensesSelectedState is Status.Success)
                        expensesSelectedState.value != it.active
                    else
                        !it.active //gastos por default


                }

                _initialAccountOperations.value =
                    if (expenses.isNotEmpty()) Status.Success(expenses)
                    else Status.Error("No hay gastos/ingresos en la cuenta.")

            }
            else -> {
                _initialAccountOperations.value = Status.Error(operationsResult.message ?: "")
            }
        }

    }

    fun selectExpenses() {
        _expensesSelected.value = Status.Success(true)
    }

    fun selectIncomes() {
        _expensesSelected.value = Status.Success(false)
    }

    suspend fun getAccountOperations(
        localAccountId: Int,
        forceUpdate: Boolean = false
    ) {
        val dateFilter = dateFilterFlow.value


        val result: Resource<List<OperationModel>> = if (dateFilter is Filter.ActiveDateFilter)
            opRepository.getAccountOperations(
                localAccountId,
                forceUpdate,
                dateFilter.startDate,
                dateFilter.endDate
            )
        else
            opRepository.getAccountOperations(localAccountId, forceUpdate, null, null)

        when (result) {
            is Resource.Success -> {
                _accountOperations.value = Status.Success(result.data)
            }
            else -> {
                _accountOperations.value = Status.Error(result.message ?: "")
            }
        }
    }

    suspend fun getBalanceDescription(localAccountId: Int) {

        val balanceResult = acRepository.getAccountBalance(localAccountId)
        val movementResult = acRepository.getAccountBalanceMovement(localAccountId)

        if (balanceResult is Resource.Success && movementResult is Resource.Success) {

            _accountBalanceData.value =
                Status.Success(Pair(balanceResult.data, movementResult.data))

            setFragmentSuccessState() //Aumentar el contador para llegar al estado success

        } else if (balanceResult is Resource.Success){
            _accountBalanceData.value = Status.Error(balanceResult.message ?: "")
            setFragmentFailureState() //Estado fallido
        }

        else{
            _accountBalanceData.value = Status.Error(movementResult.message ?: "")
            setFragmentFailureState() //Estado fallido
        }


    }

    suspend fun getAccountData(localAccountId: Int, forceUpdate: Boolean) {
        val result = acRepository.getAccountData(localAccountId, forceUpdate)

        if (result is Resource.Success){
            _accountData.value = Status.Success(result.data)
            setFragmentSuccessState() //Aumentar el contador para llegar al estado success
        }
        else{
            _accountData.value = Status.Error(result.message ?: "")
            setFragmentFailureState() //Estado fallido
        }

    }

    suspend fun setAsFavorite(accountLocalId: Int): Flow<Status<Boolean>> {
        return flow {
            emit(Status.Loading())
            val resultFavorite = acRepository.setAsDefaultAccount(accountLocalId)

            if (resultFavorite is Resource.Success) {

                getAccountData(accountLocalId, false)
                emit(Status.Success(true))

            } else emit(Status.Error(resultFavorite.message ?: ""))
        }

    }

    suspend fun deleteAccount(accountLocalId: Int): Flow<Status<Boolean>> {
        return flow {
            emit(Status.Loading())

            val resultDelete = acRepository.deleteAccount(accountLocalId)

            if (resultDelete is Resource.Success)
                emit(Status.Success(true))
            else emit(Status.Error(resultDelete.message ?: ""))
        }
    }

    private fun setFragmentSuccessState() {
        successesCount++
        if (successesCount >= 2){  //Dos operaciones a completar
            _fragmentState.value = Status.Success(true)
            successesCount = 0
        }
    }

    private fun setFragmentFailureState(){
        if(successesCount >= 0) successesCount = 0;
        //Si la otra operación aún no se ha completado, para que el contador quede en cero
        else successesCount = -1
        _fragmentState.value = Status.Error("")
    }


}