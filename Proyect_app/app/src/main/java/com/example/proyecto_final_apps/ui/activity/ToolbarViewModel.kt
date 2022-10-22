package com.example.proyecto_final_apps.ui.activity

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ToolbarViewModel: ViewModel() {

    private val _searchFlow: MutableStateFlow<String> = MutableStateFlow("")
    val searchFlow:StateFlow<String> = _searchFlow

    private val _initializedFlow: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val initialized:StateFlow<Boolean> = _initializedFlow

    fun triggerSearchFlow(text:String){
        _searchFlow.value = text
        println("DIEGO: hola")
    }

    fun initializeToolbar(){
        _initializedFlow.value = true
    }
}