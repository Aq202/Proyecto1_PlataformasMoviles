package com.example.proyecto_final_apps.helpers

import java.text.SimpleDateFormat
import java.time.LocalDate
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

    fun getFirstDayOfMonthDate():Date{
        val date = Date()
        return formatDate("01/${date.getMonthValue()}/${date.getYearValue()}")
    }

    fun Date.getDayValue():Int{
        val calendar = Calendar.getInstance()
        calendar.time = this
        return calendar[Calendar.DATE]
    }

    fun Date.getMonthValue():Int{
            val calendar = Calendar.getInstance()
            calendar.time = this
            return calendar[Calendar.MONTH] + 1
    }

    fun Date.getYearValue():Int{
        val calendar = Calendar.getInstance()
        calendar.time = this
        return calendar[Calendar.YEAR]
    }
}