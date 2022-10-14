package com.example.proyecto_final_apps.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat.getColor
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.request.CachePolicy
import com.example.proyecto_final_apps.R
import com.example.proyecto_final_apps.data.Operation
import com.example.proyecto_final_apps.helpers.format
import com.google.android.material.card.MaterialCardView

class OperationAdapter(
    private val dataSet: MutableList<Operation>,
    private val operationListener: OperationListener
) : RecyclerView.Adapter<OperationAdapter.ViewHolder>() {

    interface OperationListener {
        fun onItemClicked(operationData: Operation, position: Int)
        fun onItemPressed(operationData: Operation, position: Int)
    }

    class ViewHolder(private val view: View, private val listener: OperationListener) :
        RecyclerView.ViewHolder(view) {

        private val container: MaterialCardView =
            view.findViewById(R.id.cardView_contactItemTemplate_parentContainer)
        private val imageIconContainer: MaterialCardView =
            view.findViewById(R.id.cardView_contactItemTemplate_pictureContainer)
        private val imageIcon: ImageView =
            view.findViewById(R.id.imageView_contactItemTemplate_picture)
        private val txtTitle: TextView =
            view.findViewById(R.id.textView_accountItemTemplate_name)
        private val txtCategory: TextView =
            view.findViewById(R.id.textView_contactItemTemplate_alias)
        private val amountContainer: MaterialCardView =
            view.findViewById(R.id.cardView_accountItemTemplate_amountContainer)
        private val txtAmount: TextView =
            view.findViewById(R.id.textView_accountItemTemplate_amount)

        private lateinit var operationData: Operation

        fun setData(operation: Operation) {
            this.operationData = operation

            val (title, category, amount, active: Boolean?, imgUrl) = operation

            //agregar texto de la operacion
            txtTitle.text = operation.title
            txtCategory.text = operation.category?.name ?: "Sin categoria"
            txtAmount.text = view.context.getString(
                R.string.operation_item_amount,
                (if (active == true) "+" else "-"),
                amount.format(2)
            )

            //modificar icono
            imageIconContainer.setCardBackgroundColor(
                operation.category?.color ?: getColor(view.context, R.color.default_category)
            )

            //colocar icono de la categoria
            if (category?.img != null)
                imageIcon.setImageDrawable(operation.category?.img)
            else {
                if (imgUrl != null) {

                    //asignar imagen de origen remoto
                    imageIcon.load(imgUrl) {
                        placeholder(R.drawable.ic_loading)
                        error(R.drawable.ic_money_bag) //Imagen por default
                        memoryCachePolicy(CachePolicy.ENABLED)
                        diskCachePolicy(CachePolicy.ENABLED)
                    }
                } else imageIcon.setImageDrawable(
                    //Colocar imagen por default
                    AppCompatResources.getDrawable(
                        view.context,
                        R.drawable.ic_money_bag
                    )
                )

            }

            //modificar item de cantidad
            when (operation.active) {
                true -> {
                    amountContainer.setCardBackgroundColor(
                        getColor(
                            view.context,
                            R.color.light_green_1
                        )
                    )
                    amountContainer.strokeColor = getColor(view.context, R.color.light_green_2)
                    txtAmount.setTextColor(getColor(view.context, R.color.light_green_2))
                }
                false -> {
                    amountContainer.setCardBackgroundColor(
                        getColor(
                            view.context,
                            R.color.light_red_1
                        )
                    )
                    amountContainer.strokeColor = getColor(view.context, R.color.red)
                    txtAmount.setTextColor(getColor(view.context, R.color.red))
                }
                else -> {
                    amountContainer.setCardBackgroundColor(
                        getColor(
                            view.context,
                            R.color.light_skyblue_1
                        )
                    )
                    amountContainer.strokeColor = getColor(view.context, R.color.sky_blue)
                    txtAmount.setTextColor(getColor(view.context, R.color.sky_blue))
                }
            }


            setListeners()
        }

        private fun setListeners() {
            container.setOnClickListener {
                listener.onItemClicked(operationData, this.adapterPosition)
            }

            container.setOnLongClickListener {

                listener.onItemPressed(operationData, this.adapterPosition)
                false
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.template_operation_item, parent, false)
        return ViewHolder(view, operationListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setData(dataSet[position])
    }

    override fun getItemCount() = dataSet.size

}