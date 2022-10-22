package com.example.proyecto_final_apps.data

import android.content.Context
import android.graphics.drawable.Drawable



data class Operation(
    val id:Int,
    val title:String,
    val accountId:Int,
    val category:CategoryModel?,
    val amount:Double,
    val active:Boolean?,
    val imgUrl:String?,
    val favourite: Boolean? = null,
    val date:String,
    val description :String? = null,
)




class TestOperations(private val context:Context) {

    private val operations = mutableListOf(

        Operation(
            0,
            "Almuerzo del viernes",
            0,
            Category(context).getCategory(1),
            15.57,
            false,
            null,
            true,
            "21/10/2022",
            "Hamburguesa en Mac.",
        ),

        Operation(
            1,
            "Viaje en Uber",
            1,
            Category(context).getCategory(2),
            45.00,
            false,
            null,
            false,
            "21/10/2022",
            "Viaje en uber pagado a mi hermana",
        ),

        Operation(
            2,
            "Venta de helados",
            0,
            Category(context).getCategory(1),
            20.56,
            true,
            null,
            false,
            "21/10/2022"

        ),
        Operation(
            3,
            "Deuda",
            2,
            Category(context).getCategory(3),
            30.56,
            null,
            null,
            false,
            "21/10/2022"
        ),
        Operation(
            4,
            "Deuda con Samuel",
            2,
            Category(context).getCategory(3),
            20.86,
            false,
            "https://cdn.domestika.org/c_fill,dpr_1.0,f_auto,h_1200,pg_1,t_base_params,w_1200/v1589759117/project-covers/000/721/921/721921-original.png?1589759117",
            false,
            "21/10/2022"
        ),


    )

    fun getOperations() = operations
    fun getFavourites() : MutableList<Operation>{
        val favourites = mutableListOf<Operation>()
        operations.forEach{
            if (it.favourite == true)
                favourites.add(it)
        }
        return favourites
    }

    fun getOperationById(id:Int) :List<Operation>{
        return operations.filter { it.id == id }
    }
}