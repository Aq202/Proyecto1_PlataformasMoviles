package com.example.proyecto_final_apps.data

data class AccountModel(
    val id:Int,
    val title:String,
    val total:Double,
    val default:Boolean
)

object AccountData {

    private val _accounts = mutableListOf<AccountModel>(

        AccountModel(
            0,
            "Efectivo",
            250.84,
            true
        ),
        AccountModel(
            1,
            "Cuenta Bac",
            3560.19,
             false
        ),
        AccountModel(
            2,
            "Deudas",
            -299.54,
             false
        ),



    )

    val accounts = _accounts
    fun getAccountById (id:Int):AccountModel{
        return _accounts.filter { it.id == id }[0]
    }

}