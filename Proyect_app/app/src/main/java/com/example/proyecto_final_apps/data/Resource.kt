package com.example.proyecto_final_apps.data

sealed class Resource<T>(val message:String? = null) {

    class Success<T>(val data:T): Resource<T>()
    class Error<T>(message:String):Resource<T>(message=message)
}