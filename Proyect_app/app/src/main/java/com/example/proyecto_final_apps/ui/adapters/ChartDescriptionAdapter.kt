package com.example.proyecto_final_apps.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.proyecto_final_apps.R
import com.example.proyecto_final_apps.helpers.format
import com.example.proyecto_final_apps.ui.Components.PieChart


class ChartDescriptionAdapter(
    private val dataSet: MutableList<PieChart.PieElement>,
) : RecyclerView.Adapter<ChartDescriptionAdapter.ViewHolder>() {


    class ViewHolder(private val view: View) :
        RecyclerView.ViewHolder(view) {

        private val circle:TextView = view.findViewById(R.id.textView_pieItem_circle)
        private val txtTitle:TextView = view.findViewById(R.id.textView_pieItem_title)
        private val txtAmount:TextView = view.findViewById(R.id.textView_pieItem_amount)



        private lateinit var itemData:PieChart.PieElement

        fun setData(item:PieChart.PieElement) {
            this.itemData = item



            //agregar texto de la cuenta
            txtTitle.text = item.text
            txtAmount.text = view.context.getString(R.string.money_format, item.amount.format(2))
            circle.background.setTint(item.color)

        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.template_piechart_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setData(dataSet[position])
    }

    override fun getItemCount() = dataSet.size

}