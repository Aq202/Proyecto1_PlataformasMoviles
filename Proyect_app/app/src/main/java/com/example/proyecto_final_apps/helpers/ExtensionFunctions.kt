package com.example.proyecto_final_apps.helpers

import kotlin.math.abs

fun Double.format(digits: Int): String {
    val number = "%.${digits}f".format(abs(this))
    return if(abs(this) < 10) "0$number"
    else number
}

fun Double.addSigne(digits:Int, avoidPositive:Boolean = false):String{

    var result:String = this.format(digits)

    return if(this < 0) "- $result"
    else if(!avoidPositive) "+ $result"
    else result
}