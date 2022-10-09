package com.example.proyecto_final_apps.data

data class AccountModel(
    val id:Int,
    val title:String,
    val total:Double,
)

class Account {

    private val _accounts = mutableListOf<AccountModel>(

        AccountModel(
            0,
            "Efectivo",
            250.84
        ),
        AccountModel(
            1,
            "Cuenta Bac",
            3560.19
        ),
        AccountModel(
            2,
            "Deudas",
            -299.54
        )
    )

    val accounts = _accounts

}