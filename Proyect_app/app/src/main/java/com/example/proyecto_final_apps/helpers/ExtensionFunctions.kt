package com.example.proyecto_final_apps.helpers

import android.content.Context
import android.content.res.ColorStateList
import com.example.proyecto_final_apps.data.CategoryModel
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.math.RoundingMode
import java.text.DecimalFormat
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.roundToInt


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

fun Double.twoDecimals():String{
    return String.format("%.2f", this)
}

fun Double.getDecimal(numberOfDecimal:Int):Int{
    val decimal = this % 1
    return ( decimal * 10.0.pow(numberOfDecimal.toDouble())).toInt()
}

fun Int.twoDigits():String{

    return if(this >= 0){
        //num positivo: agregar 0 si es menor a 10
        return if(this < 10) ("0" + this) else this.toString()
        //num negativo: agregar "-" y parsear a dos digitos
    }else "-" + abs(this).twoDigits();
}


fun ChipGroup.addChip(context: Context, category: CategoryModel, backgroundColor: ColorStateList, strokeColor: ColorStateList){
    Chip(context).apply {
        id = category.id
        text = category.name
        chipIcon = category.img
        setTextColor(strokeColor)
        chipBackgroundColor = backgroundColor
        chipStrokeColor = strokeColor
        isCheckable = true
        chipIconTint = strokeColor
        isChecked = false
        isCheckedIconVisible = false
        isClickable = true
        addView(this)
    }
}

fun String.getWords(): List<String>{
    val regex = Regex("\\S*\\S")
    val result = regex.findAll(this)
    return result.toList().map{ it.value }
}

fun String.createPartFromString(): RequestBody {
    return this.toRequestBody("text/plain".toMediaTypeOrNull())
}

