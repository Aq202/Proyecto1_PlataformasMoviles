package com.example.proyecto_final_apps.presentation.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.proyecto_final_apps.R
import com.example.proyecto_final_apps.data.AccountModel
import com.example.proyecto_final_apps.helpers.addSigne
import com.example.proyecto_final_apps.helpers.format
import com.google.android.material.card.MaterialCardView



class ChartDescriptionAdapter(
    private val dataSet: MutableList<DescriptionItem>,
) : RecyclerView.Adapter<ChartDescriptionAdapter.ViewHolder>() {

    data class DescriptionItem(
        val color:Int,
        val title:String,
        val amount:Double
    )

    class ViewHolder(private val view: View) :
        RecyclerView.ViewHolder(view) {

        private val circle:TextView = view.findViewById(R.id.textView_pieItem_circle)
        private val txtTitle:TextView = view.findViewById(R.id.textView_pieItem_title)
        private val txtAmount:TextView = view.findViewById(R.id.textView_pieItem_amount)



        private lateinit var itemData:DescriptionItem

        fun setData(item:DescriptionItem) {
            this.itemData = item

            val (color, title, amount) = item

            //agregar texto de la cuenta
            txtTitle.text = title
            txtAmount.text = view.context.getString(R.string.money_format, amount.format(2))
            circle.background.setTint(color)

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