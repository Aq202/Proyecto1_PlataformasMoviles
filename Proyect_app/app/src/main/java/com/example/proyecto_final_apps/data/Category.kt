package com.example.proyecto_final_apps.data

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.core.content.ContextCompat.getColor
import com.example.proyecto_final_apps.R

enum class CategoryTypes{
    ALIMENTOS,
    TRANSPORTE,
    DEUDAS
}

data class CategoryModel(
    val id:Int,
    val name:String,
    val type:CategoryTypes,
    val img: Drawable?,
    val color: Int
)

class Category(private val context:Context) {

    private val categories = setOf(

        CategoryModel(
            1,
            "Alimentos",
            CategoryTypes.ALIMENTOS,
            getDrawable(context, R.drawable.ic_category_restaurant),
            getColor(context,R.color.food_category)
        ),
        CategoryModel(
            2,
            "Transporte",
            CategoryTypes.TRANSPORTE,
            getDrawable(context, R.drawable.ic_category_bus),
            getColor(context,R.color.transportation_category)
        ),
        CategoryModel(
            3,
            "Deudas",
            CategoryTypes.DEUDAS,
            null,
            getColor(context,R.color.default_category)
        )

    )

    fun getCategories() = categories
    fun getCategory(id:Int) = categories.find { it.id == id }
}