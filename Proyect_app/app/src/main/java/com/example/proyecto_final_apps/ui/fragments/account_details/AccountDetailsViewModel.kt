package com.example.proyecto_final_apps.ui.fragments.account_details

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import java.util.*

class AccountDetailsViewModel:ViewModel() {

    sealed class Filter{
        class ActiveDateFilter(val startDate:Date,val endDate:Date):Filter()
        object EmptyFilter:Filter()
    }

    private val _dateFilterFlow: MutableStateFlow<Filter> = MutableStateFlow(Filter.EmptyFilter)

    val dateFilterFlow : MutableStateFlow<Filter> = _dateFilterFlow

    fun addDateFilter(start:Date, end:Date){
        _dateFilterFlow.value = Filter.ActiveDateFilter(start, end)
    }

    fun removeDateFilter(){
        _dateFilterFlow.value = Filter.EmptyFilter
    }

}