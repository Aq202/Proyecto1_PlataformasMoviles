package com.example.proyecto_final_apps.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat.getColor
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.proyecto_final_apps.R
import com.example.proyecto_final_apps.data.AccountModel
import com.example.proyecto_final_apps.helpers.addSigne
import com.google.android.material.card.MaterialCardView

class AccountAdapter(
    private val dataSet: MutableList<AccountModel>,
    private val operationListener: AccountListener
) : RecyclerView.Adapter<AccountAdapter.ViewHolder>() {

    interface AccountListener {
        fun onItemClicked(operationData: AccountModel, position: Int)
        fun onItemPressed(operationData: AccountModel, position: Int)
    }

    class ViewHolder(private val view: View, private val listener: AccountListener) :
        RecyclerView.ViewHolder(view) {

        private val container: MaterialCardView =
            view.findViewById(R.id.cardView_accountItemTemplate_parentContainer)
        private val txtTitle: TextView =
            view.findViewById(R.id.textView_accountItemTemplate_name)
        private val txtDefaultLabel: TextView =
            view.findViewById(R.id.textView_accountItemTemplate_defaultLabel)
        private val amountContainer: MaterialCardView =
            view.findViewById(R.id.cardView_accountItemTemplate_amountContainer)
        private val txtAmount: TextView =
            view.findViewById(R.id.textView_accountItemTemplate_amount)
        private val imgDefaultIcon: ImageView =
            view.findViewById(R.id.imageView_accountItem_favoriteStar)

        private lateinit var accountData: AccountModel

        fun setData(account: AccountModel) {
            this.accountData = account

            val (_, title, total, default) = account

            //agregar texto de la cuenta
            txtTitle.text = title
            txtAmount.text = total.addSigne(2)
            txtDefaultLabel.text = view.context.getString(
                R.string.default_label, if (default) view.context.getString(
                    R.string.si
                ) else view.context.getString(R.string.no)
            )

            //modificar item de cantidad
            when (total < 0) {
                false -> {
                    amountContainer.setCardBackgroundColor(
                        getColor(
                            view.context,
                            R.color.light_skyblue_1
                        )
                    )
                    amountContainer.strokeColor = getColor(view.context, R.color.sky_blue)
                    txtAmount.setTextColor(getColor(view.context, R.color.sky_blue))
                }
                true -> {
                    amountContainer.setCardBackgroundColor(
                        getColor(
                            view.context,
                            R.color.light_red_1
                        )
                    )
                    amountContainer.strokeColor = getColor(view.context, R.color.red)
                    txtAmount.setTextColor(getColor(view.context, R.color.red))
                }

            }

            //ocultar estrella favorito
            if (!default)
                imgDefaultIcon.isVisible = false

            setListeners()
        }

        private fun setListeners() {
            container.setOnClickListener {
                listener.onItemClicked(accountData, this.adapterPosition)
            }

            container.setOnLongClickListener {

                listener.onItemPressed(accountData, this.adapterPosition)
                false
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.template_account_item, parent, false)
        return ViewHolder(view, operationListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setData(dataSet[position])
    }

    override fun getItemCount() = dataSet.size

}