package com.example.proyecto_final_apps.helpers

fun Double.format(digits: Int): String {
    val number = "%.${digits}f".format(this)
    return if(this < 10) "0$number"
    else number
}