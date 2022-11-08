package com.example.proyecto_final_apps.ui.Components

import android.graphics.Color
import android.graphics.Typeface
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.utils.MPPointF
import com.github.mikephil.charting.charts.PieChart
import kotlin.math.round

class PieChart( val pieChart:PieChart) {

    data class PieElement(
        val color:Int,
        var amount:Double,
        val text:String?
    );

    init {
        // on below line we are setting user percent value,
        // setting description as enabled and offset for pie chart
        pieChart.setUsePercentValues(true)
        pieChart.description.isEnabled = false
        pieChart.setExtraOffsets(5f, 10f, 5f, 5f)

        // on below line we are setting drag for our pie chart
        pieChart.dragDecelerationFrictionCoef = 0.95f

        // on below line we are setting hole
        // and hole color for pie chart
        pieChart.isDrawHoleEnabled = true
        pieChart.setHoleColor(Color.WHITE)

        // on below line we are setting circle color and alpha
        pieChart.setTransparentCircleColor(Color.TRANSPARENT)
        pieChart.setTransparentCircleAlpha(110)

        // on  below line we are setting hole radius
        pieChart.holeRadius = 58f
        pieChart.transparentCircleRadius = 61f

        // on below line we are setting center text
        pieChart.setDrawCenterText(true)

        // on below line we are setting
        // rotation for our pie chart
        pieChart.rotationAngle = 0f

        // enable rotation of the pieChart by touch
        pieChart.isRotationEnabled = true
        pieChart.isHighlightPerTapEnabled = true

        // on below line we are setting animation for our pie chart
        pieChart.animateY(1400, Easing.EaseInOutQuad)

        // on below line we are disabling our legend for pie chart
        pieChart.legend.isEnabled = false
        pieChart.setEntryLabelColor(Color.BLACK)
        pieChart.setEntryLabelTextSize(16f)

    }

    fun showData(data:List<PieElement>){

        var totalAmount:Float = 0f

        data.forEach{
            totalAmount += it.amount.toFloat()
        }

        //añadir valores

        val entries: ArrayList<PieEntry> = ArrayList()
        val colors: ArrayList<Int> = ArrayList()

        data.forEach{
            val percentage:Float = round((it.amount.toFloat() / totalAmount) * 100f)

            if(it.text != null)
                entries.add(PieEntry(percentage, it.text))
            else
                entries.add(PieEntry(percentage))

            //añadir color
            colors.add(it.color)
        }

        // on below line we are setting pie data set
        val dataSet = PieDataSet(entries, "Account Categories")

        // on below line we are setting icons.
        dataSet.setDrawIcons(false)

        // Configuración de las partes del pie
        dataSet.sliceSpace = 3f
        dataSet.iconsOffset = MPPointF(0f, 40f)
        dataSet.selectionShift = 5f


        // añadir configuracion de colores
        dataSet.colors = colors

        // Asignar data set al pie
        val data = PieData(dataSet)
        data.setValueFormatter(PercentFormatter(pieChart))
        data.setValueTextSize(16f)
        data.setValueTypeface(Typeface.DEFAULT_BOLD)
        data.setValueTextColor(Color.WHITE)

        pieChart.data = data

        // undo all highlights
        pieChart.highlightValues(null)

        // loading chart
        pieChart.invalidate()
    }
}