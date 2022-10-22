package com.example.proyecto_final_apps.ui.activity

import androidx.lifecycle.ViewModel
import com.example.proyecto_final_apps.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class BottomNavigationViewModel: ViewModel() {

    sealed class BottomNavigationItem(val itemId:Int){
        object HOME : BottomNavigationItem(R.id.item_bottomNav_home)
        object NEW: BottomNavigationItem(R.id.item_bottomNav_newOperation)
        object CONTACTS: BottomNavigationItem(R.id.item_bottomNav_contacts)
    }

    private val _selectedItem:MutableStateFlow<BottomNavigationItem> = MutableStateFlow(BottomNavigationItem.HOME)
    val selectedItem:StateFlow<BottomNavigationItem> = _selectedItem

    fun setSelectedItem(item:BottomNavigationItem){
        _selectedItem.value = item
    }


}