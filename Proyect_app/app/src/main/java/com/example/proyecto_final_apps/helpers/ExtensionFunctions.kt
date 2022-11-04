package com.example.proyecto_final_apps.helpers

import android.content.Context
import android.content.res.ColorStateList
import com.example.proyecto_final_apps.data.CategoryModel
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import java.math.RoundingMode
import java.text.DecimalFormat
import kotlin.math.abs
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

