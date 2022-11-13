package com.example.proyecto_final_apps.data.repository

import android.content.Context
import com.example.proyecto_final_apps.data.local.Database
import com.example.proyecto_final_apps.data.remote.API
import com.example.proyecto_final_apps.data.remote.ErrorParser

class DebtRepositoryImp(
    val api: API,
    val context: Context,
    val database: Database,
    val errorParser: ErrorParser
) : DebtRepository {


}