package com.example.proyecto_final_apps.ui.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.request.CachePolicy
import com.example.proyecto_final_apps.R
import com.example.proyecto_final_apps.data.ContactModel
import com.google.android.material.card.MaterialCardView


class ContactAdapter(
    private val dataSet: MutableList<ContactModel>,
    private val contactListener: ContactListener
) : RecyclerView.Adapter<ContactAdapter.ViewHolder>() {

    interface ContactListener {
        fun onItemClicked(contactData: ContactModel, position: Int)
    }

    class ViewHolder(private val view: View, private val listener: ContactListener) :
        RecyclerView.ViewHolder(view) {

        private val container: MaterialCardView =
            view.findViewById(R.id.cardView_contactItemTemplate_parentContainer)
        private val imageIconContainer: MaterialCardView =
            view.findViewById(R.id.cardView_contactItemTemplate_pictureContainer)
        private val imageIcon: ImageView =
            view.findViewById(R.id.imageView_contactItemTemplate_picture)
        private val txtName: TextView =
            view.findViewById(R.id.textView_accountItemTemplate_name)
        private val txtAlias: TextView =
            view.findViewById(R.id.textView_contactItemTemplate_alias)


        private lateinit var contactData: ContactModel

        @SuppressLint("SetTextI18n")
        fun setData(contact: ContactModel) {
            this.contactData = contact

            val (alias, name, lastName, _, pictureUrl) = contact

            //agregar texto del contacto
            txtName.text = "$name $lastName"
            txtAlias.text = alias

            //agregar foto
            imageIcon.load(pictureUrl) {
                placeholder(R.drawable.ic_loading)
                error(R.drawable.ic_default_user) //Imagen por default
                memoryCachePolicy(CachePolicy.ENABLED)
                diskCachePolicy(CachePolicy.ENABLED)
            }

            setListeners()
        }

        private fun setListeners() {
            container.setOnClickListener {
                listener.onItemClicked(contactData, this.adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.template_contact_item, parent, false)
        return ViewHolder(view, contactListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setData(dataSet[position])
    }

    override fun getItemCount() = dataSet.size

}