package com.example.proyecto_final_apps.data

import android.content.Context
import android.graphics.drawable.Drawable



data class Operation(
    val title:String,
    val category:CategoryModel?,
    val amount:Double,
    val active:Boolean?,
    val imgUrl:String?,
    val favourite: Boolean? = null,
    val selected: Boolean? = null
)




class TestOperations(private val context:Context) {

    private val operations = mutableListOf(

        Operation(
            "Almuerzo del viernes",
            Category(context).getCategory(1),
            15.57,
            false,
            null,
            true
        ),

        Operation(
            "Viaje en Uber",
            Category(context).getCategory(2),
            45.00,
            false,
            null,
            false
        ),

        Operation(
            "Venta de helados",
            Category(context).getCategory(1),
            20.56,
            true,
            null,
            false
        ),
        Operation(
            "Deuda",
            Category(context).getCategory(3),
            30.56,
            null,
            null,
            false
        ),
        Operation(
            "Deuda con Samuel",
            Category(context).getCategory(3),
            20.86,
            false,
            "https://cdn.domestika.org/c_fill,dpr_1.0,f_auto,h_1200,pg_1,t_base_params,w_1200/v1589759117/project-covers/000/721/921/721921-original.png?1589759117",
            false
        ),
        Operation(
            "Almuerzo del viernes",
            Category(context).getCategory(1),
            15.57,
            false,
            null,
            false
        ),

        Operation(
            "Viaje en Uber",
            Category(context).getCategory(2),
            45.00,
            false,
            null,
            true
        ),

        Operation(
            "Venta de helados",
            Category(context).getCategory(1),
            20.56,
            true,
            null,
            false
        ),
        Operation(
            "Deuda",
            Category(context).getCategory(3),
            30.56,
            null,
            null,
            false
        ),
        Operation(
            "Deuda con Samuel",
            Category(context).getCategory(3),
            20.86,
            false,
            "https://cdn.domestika.org/c_fill,dpr_1.0,f_auto,h_1200,pg_1,t_base_params,w_1200/v1589759117/project-covers/000/721/921/721921-original.png?1589759117",
            null
        ),
        Operation(
            "Almuerzo del viernes",
            Category(context).getCategory(1),
            15.57,
            false,
            null,
            false
        ),

        Operation(
            "Viaje en Uber",
            Category(context).getCategory(2),
            45.00,
            false,
            null,
            false
        ),

        Operation(
            "Venta de helados",
            Category(context).getCategory(1),
            20.56,
            true,
            null,
            false
        ),
        Operation(
            "Deuda",
            Category(context).getCategory(3),
            30.56,
            null,
            null,
            true
        ),
        Operation(
            "Deuda con Samuel",
            Category(context).getCategory(3),
            20.86,
            false,
            "https://cdn.domestika.org/c_fill,dpr_1.0,f_auto,h_1200,pg_1,t_base_params,w_1200/v1589759117/project-covers/000/721/921/721921-original.png?1589759117",
            true
        ),
        Operation(
            "Almuerzo del viernes",
            Category(context).getCategory(1),
            15.57,
            false,
            null,
            false
        ),

        Operation(
            "Viaje en Uber",
            Category(context).getCategory(2),
            45.00,
            false,
            null,
            null
        ),

        Operation(
            "Venta de helados",
            Category(context).getCategory(1),
            20.56,
            true,
            null,
            true
        ),
        Operation(
            "Deuda",
            Category(context).getCategory(3),
            30.56,
            null,
            null,
            false
        ),
        Operation(
            "Deuda con Samuel",
            Category(context).getCategory(3),
            20.86,
            false,
            "https://cdn.domestika.org/c_fill,dpr_1.0,f_auto,h_1200,pg_1,t_base_params,w_1200/v1589759117/project-covers/000/721/921/721921-original.png?1589759117",
            false
        ),
        Operation(
            "Almuerzo del viernes",
            Category(context).getCategory(1),
            15.57,
            false,
            null,
            true
        ),

        Operation(
            "Viaje en Uber",
            Category(context).getCategory(2),
            45.00,
            false,
            null,
            true
        ),

        Operation(
            "Venta de helados",
            Category(context).getCategory(1),
            20.56,
            true,
            null,
            null
        ),
        Operation(
            "Deuda",
            Category(context).getCategory(3),
            30.56,
            null,
            null,
            true
        ),
        Operation(
            "Deuda con Samuel",
            Category(context).getCategory(3),
            20.86,
            false,
            "https://cdn.domestika.org/c_fill,dpr_1.0,f_auto,h_1200,pg_1,t_base_params,w_1200/v1589759117/project-covers/000/721/921/721921-original.png?1589759117",
            false
        ),
        Operation(
            "Almuerzo del viernes",
            Category(context).getCategory(1),
            15.57,
            false,
            null,
            null
        ),

        Operation(
            "Viaje en Uber",
            Category(context).getCategory(2),
            45.00,
            false,
            null,
        ),

        Operation(
            "Venta de helados",
            Category(context).getCategory(1),
            20.56,
            true,
            null,
        ),
        Operation(
            "Deuda",
            Category(context).getCategory(3),
            30.56,
            null,
            null,
        ),
        Operation(
            "Deuda con Samuel",
            Category(context).getCategory(3),
            20.86,
            false,
            "https://cdn.domestika.org/c_fill,dpr_1.0,f_auto,h_1200,pg_1,t_base_params,w_1200/v1589759117/project-covers/000/721/921/721921-original.png?1589759117",
        ),
        Operation(
            "Almuerzo del viernes",
            Category(context).getCategory(1),
            15.57,
            false,
            null,
        ),

        Operation(
            "Viaje en Uber",
            Category(context).getCategory(2),
            45.00,
            false,
            null,
        ),

        Operation(
            "Venta de helados",
            Category(context).getCategory(1),
            20.56,
            true,
            null,
        ),
        Operation(
            "Deuda",
            Category(context).getCategory(3),
            30.56,
            null,
            null,
        ),
        Operation(
            "Deuda con Samuel",
            Category(context).getCategory(3),
            20.86,
            false,
            "https://cdn.domestika.org/c_fill,dpr_1.0,f_auto,h_1200,pg_1,t_base_params,w_1200/v1589759117/project-covers/000/721/921/721921-original.png?1589759117",
        ),
        Operation(
            "Almuerzo del viernes",
            Category(context).getCategory(1),
            15.57,
            false,
            null,
        ),

        Operation(
            "Viaje en Uber",
            Category(context).getCategory(2),
            45.00,
            false,
            null,
        ),

        Operation(
            "Venta de helados",
            Category(context).getCategory(1),
            20.56,
            true,
            null,
        ),
        Operation(
            "Deuda",
            Category(context).getCategory(3),
            30.56,
            null,
            null,
        ),
        Operation(
            "Deuda con Samuel",
            Category(context).getCategory(3),
            20.86,
            false,
            "https://cdn.domestika.org/c_fill,dpr_1.0,f_auto,h_1200,pg_1,t_base_params,w_1200/v1589759117/project-covers/000/721/921/721921-original.png?1589759117",
        ),
        Operation(
            "Almuerzo del viernes",
            Category(context).getCategory(1),
            15.57,
            false,
            null,
        ),

        Operation(
            "Viaje en Uber",
            Category(context).getCategory(2),
            45.00,
            false,
            null,
        ),

        Operation(
            "Venta de helados",
            Category(context).getCategory(1),
            20.56,
            true,
            null,
        ),
        Operation(
            "Deuda",
            Category(context).getCategory(3),
            30.56,
            null,
            null,
        ),
        Operation(
            "FIIIIN",
            Category(context).getCategory(3),
            20.86,
            false,
            "https://cdn.domestika.org/c_fill,dpr_1.0,f_auto,h_1200,pg_1,t_base_params,w_1200/v1589759117/project-covers/000/721/921/721921-original.png?1589759117",
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
}