package com.example.proyecto_final_apps.ui.util

interface ErrorType{

}

sealed class Status<T>(){
    class Default<T>:Status<T>()
    class Loading<T>:Status<T>()
    class Reloading<T>:Status<T>()
    class Success<T>(val value:T):Status<T>()
    class Error<T>(val error:String, val errorType: ErrorType? = null):Status<T>()

}