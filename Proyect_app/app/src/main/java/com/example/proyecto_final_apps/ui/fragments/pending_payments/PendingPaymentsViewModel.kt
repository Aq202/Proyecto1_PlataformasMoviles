package com.example.proyecto_final_apps.ui.fragments.pending_payments

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyecto_final_apps.data.Resource
import com.example.proyecto_final_apps.data.local.entity.DebtAcceptedModel
import com.example.proyecto_final_apps.data.local.entity.UserModel
import com.example.proyecto_final_apps.domain.DebtDomain
import com.example.proyecto_final_apps.ui.util.Status
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PendingPaymentsViewModel @Inject constructor(private val debtDomain: DebtDomain) : ViewModel() {

    private val _recentDebts = MutableStateFlow<Status<List<Pair<DebtAcceptedModel, UserModel>>>>(Status.Default())
    val recentDebts: StateFlow<Status<List<Pair<DebtAcceptedModel, UserModel>>>> = _recentDebts

    fun getRecentDebts(forceUpdate:Boolean){

        _recentDebts.value = Status.Loading()

        viewModelScope.launch {

            val result = debtDomain.getDebtList(forceUpdate)
            if(result is Resource.Success) _recentDebts.value = Status.Success(result.data)
            else _recentDebts.value = Status.Error(result.message ?: "No se obtuvieron resultados.")
        }
    }
}