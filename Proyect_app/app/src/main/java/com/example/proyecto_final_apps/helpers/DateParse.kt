package com.example.proyecto_final_apps.helpers

import java.text.SimpleDateFormat
import java.util.*

object DateParse {

    val months = listOf(
        "enero",
        "febrero",
        "marzo",
        "abril",
        "mayo",
        "junio",
        "julio",
        "agosto",
        "septiembre",
        "octubre",
        "noviembre",
        "diciembre"
    )

    fun formatDate(date: String): Date {
        val format = SimpleDateFormat(DATE_FORMAT, Locale.US)
        return format.parse(date) as Date
    }

    fun dateToWordsFormat(date: Date):String{
        val calendar = Calendar.getInstance()
        calendar.time = date
        val day = calendar[Calendar.DATE]
        val month = calendar[Calendar.MONTH]
        val year = calendar[Calendar.YEAR]

        return "$day de ${months[month]} de $year"
    }
}