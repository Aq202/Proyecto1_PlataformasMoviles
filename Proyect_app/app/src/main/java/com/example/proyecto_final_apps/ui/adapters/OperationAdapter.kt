package com.example.proyecto_final_apps.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat.getColor
import androidx.core.view.setPadding
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.request.CachePolicy
import coil.transform.CircleCropTransformation
import com.example.proyecto_final_apps.R
import com.example.proyecto_final_apps.data.Category
import com.example.proyecto_final_apps.data.CategoryModel
import com.example.proyecto_final_apps.data.local.entity.OperationModel
import com.example.proyecto_final_apps.data.local.entity.getCategory
import com.example.proyecto_final_apps.helpers.format
import com.google.android.material.card.MaterialCardView

data class OperationItem(
    val localId:Int,
    val remoteId:String ?,
    val title:String,
    val category:CategoryModel?,
    val amount:Double,
    val active:Boolean,
    val imgUrl:String?,
    val favorite:Boolean = false,
)

class OperationAdapter(
    private val dataSet: MutableList<OperationItem>,
    private val operationListener: OperationListener
) : RecyclerView.Adapter<OperationAdapter.ViewHolder>() {

    interface OperationListener {
        fun onItemClicked(operationData: OperationItem, position: Int)
        fun onItemPressed(operationData: OperationItem, position: Int)
    }

    class ViewHolder(private val view: View, private val listener: OperationListener) :
        RecyclerView.ViewHolder(view) {

        private val container: MaterialCardView =
            view.findViewById(R.id.cardView_operationItemTemplate_parentContainer)
        private val imageIconContainer: MaterialCardView =
            view.findViewById(R.id.cardView_operationItemTemplate_iconContainer)
        private val imageIcon: ImageView =
            view.findViewById(R.id.imageView_operationItemTemplate_icon)
        private val txtTitle: TextView =
            view.findViewById(R.id.textView_operationItemTemplate_title)
        private val txtCategory: TextView =
            view.findViewById(R.id.textView_operationItemTemplate_account)
        private val amountContainer: MaterialCardView =
            view.findViewById(R.id.cardView_operationItemTemplate_amountContainer)
        private val txtAmount: TextView =
            view.findViewById(R.id.textView_operationItemTemplate_amount)
        private val favouriteIcon: MaterialCardView =
            view.findViewById(R.id.cardView_operationItemTemplate_favourite)

        private lateinit var operationData: OperationItem

        fun setData(operation: OperationItem) {
            this.operationData = operation


            //agregar texto de la operacion
            txtTitle.text = operation.title
            txtCategory.text = operation.category?.name ?: "Sin categoria"
            txtAmount.text = view.context.getString(
                R.string.operation_item_amount,
                (if (operation.active) "+" else "-"),
                operation.amount.format(2)
            )

            //modificar icono
            imageIconContainer.setCardBackgroundColor(
                if(operation.imgUrl != null) getColor(view.context, R.color.white)  else operation.category?.color ?: getColor(view.context, R.color.default_category)
            )

            if (operation.imgUrl != null) {

                imageIcon.setPadding(0) //retirar el padding

                //asignar imagen de origen remoto
                imageIcon.load(operation.imgUrl) {
                    placeholder(R.drawable.ic_loading)
                    error(R.drawable.ic_default_user) //Imagen por default
                    memoryCachePolicy(CachePolicy.ENABLED)
                    diskCachePolicy(CachePolicy.ENABLED)
                }
            }else if(operation.category?.img != null)
                imageIcon.setImageDrawable(operation.category.img) //Imagen de categoria
            else {
                imageIcon.setImageDrawable(
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

            //icono de operacion favorita
            if(operation.favorite)
                favouriteIcon.visibility = View.VISIBLE

            setListeners()
        }

        private fun setListeners() {
            container.setOnLongClickListener {
                listener.onItemPressed(operationData, this.adapterPosition)
                true
            }
            container.setOnClickListener {
                listener.onItemClicked(operationData, this.adapterPosition)
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