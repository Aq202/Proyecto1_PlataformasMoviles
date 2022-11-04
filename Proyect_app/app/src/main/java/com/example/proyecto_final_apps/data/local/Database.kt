package com.example.proyecto_final_apps.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.proyecto_final_apps.data.local.dao.OperationDao
import com.example.proyecto_final_apps.data.local.dao.UserDao
import com.example.proyecto_final_apps.data.local.entity.OperationModel
import com.example.proyecto_final_apps.data.local.entity.UserModel

@Database(
    entities = [UserModel::class, OperationModel::class],
    version=2
)
abstract class Database : RoomDatabase() {
    abstract fun userDao():UserDao
    abstract fun operationDao():OperationDao
}